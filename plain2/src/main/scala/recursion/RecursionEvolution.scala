package recursion

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RecursionEvolutionSpec extends AnyFunSpec with Matchers {
  import RecursionEvolution._
  import TestData._

  /** different implementations */
//  import step1._
//  import step2._
//  import step4._
//  import step5._
//  import step6._
//  import step7._
//  import step8.step81._
//  import step8.step82._
//  import step8.step83._
//  import step8.step84._
//  import step8.step85._
//  import step8.step86._
  import step8.step87._

  describe("tests") {
    it("len") {
      len(data) shouldEqual expLen
    }

    it("sum") {
      sum(data) shouldEqual expSum
    }

    it("product") {
      product(data) shouldEqual expProduct
    }

    it("asString") {
      asString(data) shouldEqual expToString
    }
  }

}

object RecursionEvolution {

  trait Problems {
    def len(xs: List[Int]): Int = ???
    def sum(xs: List[Int]): Int = ???
    def product(xs: List[Int]): Int = ???
    def asString(xs: List[Int]): String = ???
  }

  /** plain recursion via pattern matching */
  object step1 extends Problems {

    override def len(xs: List[Int]): Int = xs match {
      case Nil => 0
      case _ :: t =>
        val residual: Int = len(t)
        1 + residual
    }

    override def sum(xs: List[Int]): Int = xs match {
      case Nil => 0
      case h :: t =>
        val residual: Int = sum(t)
        h + residual
    }

    override def product(xs: List[Int]): Int = xs match {
      case Nil => 1
      case h :: t =>
        val residual: Int = product(t)
        h * residual
    }

    override def asString(xs: List[Int]): String = xs match {
      case Nil => "nil"
      case h :: t =>
        val residual: String = asString(t)
        h.toString + " :: " + residual
    }

  }

  /** generalization via passing combiner function and base value */
  object step2 extends Problems {

    def traverse(xs: List[Int], baseCase: => Int, combinerFn: (Int, Int) => Int): Int =
      xs match {
        case Nil => baseCase
        case h :: t =>
          val residual = traverse(t, baseCase, combinerFn)
          combinerFn(h, residual)
      }

    override def len(xs: List[Int]): Int =
      traverse(xs, 0, (_, b) => 1 + b)

    override def sum(xs: List[Int]): Int =
      traverse(xs, 0, (a, b) => a + b)

    override def product(xs: List[Int]): Int =
      traverse(xs, 1, (a, b) => a * b)

    override def asString(xs: List[Int]): String =
      sys.error("can't be generalized due to fixed return type")
  }

  /** generalization on the source datatype */
  object step3 {
    def traverse[A](xs: List[A], baseCase: => Int, combinerFn: (A, Int) => Int): Int =
      xs match {
        case Nil => baseCase
        case h :: t =>
          val residual = traverse(t, baseCase, combinerFn)
          combinerFn(h, residual)
      }
  }

  /** generalization on the source and return datatype */
  object step4 extends Problems {

    def traverse[A, R](xs: List[A])(baseCase: => R, combinerFn: (A, R) => R): R =
      xs match {
        case Nil => baseCase
        case h :: t =>
          val residual = traverse(t)(baseCase, combinerFn)
          combinerFn(h, residual)
      }

    override def len(xs: List[Int]): Int =
      traverse(xs)(0, (_, r: Int) => 1 + r)

    override def sum(xs: List[Int]): Int =
      traverse(xs)(0, (a, r: Int) => a + r)

    override def product(xs: List[Int]): Int =
      traverse(xs)(1, (a, r: Int) => a * r)

    override def asString(xs: List[Int]): String =
      traverse(xs)("nil", (a, r: String) => a.toString + " :: " + r)

  }

  /** separating mapper from combiner */
  object step5 extends Problems {

    def traverse[A, R](xs: List[A])(mapper: A => R)(baseCase: => R, combinerFn: (R, R) => R): R =
      xs match {
        case Nil => baseCase
        case h :: t =>
          val residual = traverse(t)(mapper)(baseCase, combinerFn)
          combinerFn(mapper(h), residual)
      }

    override def asString(xs: List[Int]): String =
      traverse(xs)(_.toString)("nil", (r1, r2) => s"$r1 :: $r2")

  }

  /** localizing iteration */
  object step6 extends Problems {

    def traverse[A, R](xs: List[A])(mapper: A => R)(baseCase: => R, combinerFn: (R, R) => R): R = {

      def loop(xs: List[A]): R = xs match {
        case Nil => baseCase
        case h :: t =>
          val residual = loop(t)
          combinerFn(mapper(h), residual)
      }

      loop(xs)
    }

    override def asString(xs: List[Int]): String =
      traverse(xs)(_.toString)("nil", (r1, r2) => s"$r1 :: $r2")

  }

  /** removing source data parameter from declaration */
  object step7 extends Problems {

    def traverse[A, R](mapper: A => R)(baseCase: => R, combinerFn: (R, R) => R) = (xs: List[A]) => {

      def loop(xs: List[A]): R = xs match {
        case Nil => baseCase
        case h :: t =>
          val residual = loop(t)
          combinerFn(mapper(h), residual)
      }

      loop(xs)
    }

    val traverseForToString: List[Int] => String = traverse { x: Int => x.toString }("nil", (r1, r2) => s"$r1 :: $r2")

    override def asString(xs: List[Int]): String = traverseForToString(xs)

  }

  /** abstracting over pattern matching */
  object step8 {

    /** how to decompose source structure into something mappable */
    def projectList[A] = (xs: List[A]) =>
      xs match {
        case Nil    => None
        case h :: t => Some((h, t))
      }

    /** how to represent current element */
    val mapper: Int => String = _.toString

    /** algebra definition, how to fold that to the value */
    val algebra: Option[(Int, String)] => String = {
      case None         => "nil"
      case Some((a, r)) => mapper(a) + " :: " + r
    }

    object step81 extends Problems {

      def traverse[A, R](mapper: A => R)(base: => R, combine: (R, R) => R) = (xs: List[A]) => {

        def loop(xs: List[A]): R = projectList(xs) match {
          case None => base
          case Some((h, t)) =>
            val residual = loop(t)
            combine(mapper(h), residual)
        }

        loop(xs)
      }

      val traverseForToString: List[Int] => String = traverse { x: Int => x.toString }("nil", (r1, r2) => s"$r1 :: $r2")

      override def asString(xs: List[Int]): String = traverseForToString(xs)

    }

    object step82 extends Problems {

      def traverse[A, R](
          mapper: A => R,
        )(base: => R,
          combine: (R, R) => R,
          project: List[A] => Option[(A, List[A])],
        ) = (xs: List[A]) => {

        def step(xs: List[A]): R = project(xs) match {
          case None         => base
          case Some((h, t)) => combine(mapper(h), step(t))
        }

        step(xs)
      }

      val traverseForToString: List[Int] => String = traverse { x: Int =>
        x.toString
      }(
        "nil",
        (r1, r2) => s"$r1 :: $r2",
        projectList,
      )

      override def asString(xs: List[Int]): String = traverseForToString(xs)

    }

    /** pulling out base + combiner from the signature */
    object step83 extends Problems {

      def fold[A, R](mapper: A => R)(op: Option[(R, R)] => R, project: List[A] => Option[(A, List[A])]) =
        (xs: List[A]) => {

          def step(xs: List[A]): R = project(xs) match {
            case None         => op(None)
            case Some((h, t)) => op(Some(mapper(h), step(t)))
          }

          step(xs)
        }

      val traverseForToString: List[Int] => String = fold { x: Int =>
        x.toString
      }(
        {
          case None           => "nil"
          case Some((r1, r2)) => s"$r1 :: $r2"
        },
        projectList,
      )

      override def asString(xs: List[Int]): String = traverseForToString(xs)
    }

    /** pulling out mapper from the signature */
    object step84 extends Problems {

      def fold[A, R](op: Option[(A, R)] => R, project: List[A] => Option[(A, List[A])]) = (xs: List[A]) => {

        def step(xs: List[A]): R = project(xs) match {
          case None         => op(None)
          case Some((h, t)) => op(Some(h, step(t)))
        }

        step(xs)
      }

      val traverseForToString: List[Int] => String = fold(algebra, projectList)

      override def asString(xs: List[Int]): String = traverseForToString(xs)

    }

    /** eliminating pattern matching, mapper from signature */
    object step85 extends Problems {

      def fold[A, R](op: Option[(A, R)] => R, project: List[A] => Option[(A, List[A])]) = (xs: List[A]) => {

        def step(xs: List[A]): R =
          op(project(xs).map { case (h, t) => (h, step(t)) })

        step(xs)
      }

      val traverseForToString: List[Int] => String = fold(algebra, projectList)

      override def asString(xs: List[Int]): String = traverseForToString(xs)

    }

    trait Functor[F[_]] {
      def map[X, Y](fa: F[X])(f: X => Y): F[Y]
    }

    implicit val fOpt: Functor[Option] = new Functor[Option] {
      override def map[X, Y](fa: Option[X])(f: X => Y): Option[Y] = fa.map(f)
    }

    /** eliminating Option and introducing Functor from signature */
    object step86 extends Problems {

      def fold[A, R, F[_]](op: F[(A, R)] => R, project: List[A] => F[(A, List[A])])(implicit F: Functor[F]) =
        (xs: List[A]) => {

          def step(tail: List[A]): R = op(F.map(project(tail)) { case (h, t) => (h, step(t)) })

          step(xs)
        }

      /** implementation */
      val traverseForToString: List[Int] => String = fold(algebra, projectList[Int])

      /** problem solution */
      override def asString(xs: List[Int]): String = traverseForToString(xs)

    }

    /** eliminating List for the source structure and introducing Functor from signature */
    object step87 extends Problems {

      def cata[A, R, F[_], S[_]](
          projectorF: S[A] => F[(A, S[A])],
          algebraF: F[(A, R)] => R,
        )(
          implicit F: Functor[F],
        ): S[A] => R = xs => {

        def step(tail: S[A]): R = algebraF(F.map(projectorF(tail)) { case (h, t) => (h, step(t)) })

        step(xs)
      }

      val projector: List[Int] => Option[(Int, List[Int])] = projectList[Int]

      /** implementation */
      val traverseForToString: List[Int] => String = cata(projector, algebra)

      /** problem solution */
      override def asString(xs: List[Int]): String = traverseForToString(xs)

    }

  }

}
