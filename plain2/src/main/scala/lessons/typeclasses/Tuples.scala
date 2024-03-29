package lessons.typeclasses

object Tuples extends App {

  sealed trait Ordering[A] {
    def compare(left: A, right: A): Int
  }

  object Ordering {
    def apply[A](implicit ev: Ordering[A]): Ordering[A] = ev
  }

  implicit val compareInt: Ordering[Int] = new Ordering[Int] {
    override def compare(left: Int, right: Int): Int = left - right
  }

  implicit val compareString: Ordering[String] = new Ordering[String] {
    override def compare(left: String, right: String): Int = left.length - right.length
  }

  Ordering[Int].compare(1, 1)
  Ordering[String].compare("a", "ab")

  type User = (String, Int)

  implicit val compareUser: Ordering[User] = new Ordering[(String, Int)] {
    override def compare(left: (String, Int), right: (String, Int)): Int = {
      val comp1 = Ordering[String].compare(left._1, right._1)
      val comp2 = Ordering[Int].compare(left._2, right._2)

      if(comp1 != 0) comp1 else comp2
    }
  }

  Ordering[User].compare(("a", 1), ("b", 1))

  implicit def inductionStep[A, B](implicit
                                   headCompare: Ordering[A],
                                   tailCompare: Ordering[B]): Ordering[(A, B)] = new Ordering[(A, B)] {
    override def compare(left: (A, B), right: (A, B)) = {
      val comp1 = Ordering[A].compare(left._1, right._1)
      val comp2 = Ordering[B].compare(left._2, right._2)

      if(comp1 == 0) comp2 else comp1
    }
  }

  val x: Int = Ordering[(String, (String, Int))].compare(("a", ("b", 1)), ("a", ("a", 1)))
  println(x)
}
