package cats.kleisli

import cats.Id
import cats.data.{Kleisli, Reader}

object ReaderApp extends App {
  val upper = Reader((text: String) => text.toUpperCase)
  val greet = Reader((name: String) => s"Hello $name")

  val comb1: Kleisli[Id, String, String] = upper.compose(greet)
  val comb2: Kleisli[Id, String, String] = upper.andThen(greet)
  val result: Id[String] = comb1.run("Bob")
  println(s"result = $result")

}
