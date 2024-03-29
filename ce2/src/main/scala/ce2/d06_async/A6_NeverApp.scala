package ce2.d06_async

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import ce2.common.debug.DebugHelper

object A6_NeverApp extends IOApp {

  val never: IO[Nothing] =
    IO.async { callback =>
      /** callback is never called, value will never be produced */
      ()
    }

  val app: IO[Nothing] = never
    .guarantee(IO("never started now...").debug.void)

  override def run(args: List[String]): IO[ExitCode] =
    app.as(ExitCode.Success)

}
