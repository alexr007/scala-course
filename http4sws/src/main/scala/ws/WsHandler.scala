package ws

import cats.effect.Ref
import cats.effect.Temporal
import cats.effect.kernel.Concurrent
import cats.effect.kernel.Ref
import cats.effect.std.Queue
import cats.syntax.all.*
import fs2.Pipe
import fs2.Stream
import fs2.concurrent.Topic
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.Response
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import scala.concurrent.duration.*
import ws.core.*

class WsHandler[F[_]: Concurrent: Temporal](
    topic: Topic[F, OutputMsg],
    lh: LogicHandlerOld[F],
    protocol: Protocol[F]) {

  def make(wsb: WebSocketBuilder2[F]): F[Response[F]] =
    for {
      uRef   <- Ref.of[F, Option[User]](None)
      uQueue <- Queue.unbounded[F, OutputMsg]
      ws     <- wsb.build( // TODO: think how to implement in terms of Pipe[F, WebSocketFrame, WebSocketFrame]
                  send(uQueue, uRef),
                  receive(uQueue, uRef)
                )
    } yield ws

  private def send(
      uQueue: Queue[F, OutputMsg],
      uRef: Ref[F, Option[User]]
    ): Stream[F, WebSocketFrame] = {

    def uStream: Stream[F, WebSocketFrame] =
      Stream
        .fromQueueUnterminated(uQueue)
        .filter {
          case DiscardMessage => false
          case _              => true
        }
        .mapFilter(msgToWsf)

    def topicStream: Stream[F, WebSocketFrame] =
      topic
        .subscribe(maxQueued = 1000)
        .evalFilter(filterMsgF(_, uRef))
        .mapFilter(msgToWsf)

    Stream(uStream, topicStream).parJoinUnbounded
  }

  private def filterMsgF(
      msg: OutputMsg,
      userRef: Ref[F, Option[User]]
    ): F[Boolean] =
    msg match {
      case DiscardMessage      => false.pure[F]
      case msg: MessageForUser =>
        userRef.get.map {
          case Some(u) => msg.isForUser(u)
          case None    => false
        }
      case _                   => true.pure[F]
    }

  private def msgToWsf(msg: OutputMsg): Option[WebSocketFrame] =
    msg match {
      case KeepAlive          => WebSocketFrame.Ping().some
      case msg: OutputMessage => WebSocketFrame.Text(msg.asJson.noSpaces).some
      case DiscardMessage     => None
    }

  private def wsKeepAliveStream: Stream[F, Nothing] =
    Stream.awakeEvery[F](30.seconds).as(KeepAlive).through(topic.publish)

  private def wsKeepAliveStream(q: Queue[F, OutputMsg]): Stream[F, Unit] =
    Stream.awakeEvery[F](30.seconds).as(KeepAlive).evalTap(q.offer).void

  private def receive(
      uQueue: Queue[F, OutputMsg],
      uRef: Ref[F, Option[User]],
    ): Pipe[F, WebSocketFrame, Unit] =
    _.flatMap {
      /** WebSocketFrame => ListMessages */
      case WebSocketFrame.Text(text, _) => Stream.evalSeq(lh.parse(uRef, text))
      case WebSocketFrame.Close(_)      => Stream.evalSeq(protocol.disconnect(uRef))
      case _                            => Stream.empty
    }.evalMap { m =>
      uRef.get.flatMap {
        case Some(_) => topic.publish1(m).void
        case _       => uQueue.offer(m)
      }
    }.concurrently(wsKeepAliveStream(uQueue))

}
