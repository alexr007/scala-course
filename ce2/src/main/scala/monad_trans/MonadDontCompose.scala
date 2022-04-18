package monad_trans

import monad_trans.monads.{IO, State}

import scala.io.StdIn

object MonadDontCompose extends App {

  /**
    * State code
    */
  type Stack = List[String]

  def push(x: String): State[Stack, Unit] = State[Stack, Unit] {
    xs => (x :: xs, ())
  }

  /**
    * `IO` functions
    */
  def getLine: IO[String] = IO(StdIn.readLine())

  def putStrLn(s: String): IO[Unit] = IO(println(s))

  /**
    * main loop: prompt a user for some input, then push that input
    * onto a stack. you'll get errors when you try to run this
    * code, so it's commented-out.
    */
  //    val res = for {
  //        _     <- putStrLn("Type anything:")
  //        input <- getLine
  //        _     <- push(input)
  //        _     <- putStrLn(s"Input: $input")
  //    } yield ()

}
