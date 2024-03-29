package shapelss.book

import shapeless.Generic.Aux
import shapeless._
import shapeless.ops._

object C01 extends App {
  case class Employee(name: String, number: Int, manager: Boolean)
  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  def employeeCsv(e: Employee): List[String] = List(e.name, e.number.toString, e.manager.toString)
  def iceCreamCsv(c: IceCream): List[String] = List(c.name, c.numCherries.toString, c.inCone.toString)

  /** in terms of representation they are the same */
  val genE: Aux[Employee, String :: Int :: Boolean :: HNil] = Generic[Employee]
  val genI: Aux[IceCream, String :: Int :: Boolean :: HNil] = Generic[IceCream]

  /** {{{
    * Aux[Employee, String :: Int :: Boolean :: HNil] = Generic[Employee] {
    *   Repr = String :: Int :: Boolean :: HNil
    * }
    *   .to: A => HList
    *   .from: HList => A
    * }}}
    */

  // 1. creating the object
  val emp: Employee = Employee("Jim", 33, true)
  // 2. converting to generic representation
  val hl: String :: Int :: Boolean :: HNil = genE.to(emp)
  // 3. converting ack to another type
  val ic: IceCream = genI.from(hl)

  def genericCsv(gen: String :: Int :: Boolean :: HNil): List[String] = List(gen(0), gen(1).toString, gen(2).toString)

}
