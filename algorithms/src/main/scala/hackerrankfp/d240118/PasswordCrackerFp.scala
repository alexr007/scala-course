package hackerrankfp.d240118

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/** https://www.hackerrank.com/challenges/password-cracker-fp/problem */
object PasswordCrackerFp {

  def fitsAt(idx: Int, part: String, whole: String): Boolean = (idx + part.length <= whole.length) && part.indices.forall(i => part(i) == whole(idx + i))

  def isSolvable(words: Iterable[String], phrase: String): Boolean = {
    val lettersHave = words.flatten.toSet
    val lettersNeed = phrase.toSet
    lettersNeed.forall(lettersHave.contains)
  }

  def solve(words: Iterable[String], phrase: String) = {

    case class Solution(private val get: List[String]) {
      def represent: String = get.mkString(" ")
    }
    trait State {
      def done: Option[Solution]
      def tryCollect(part: String): Option[State]
    }
    object State {
      private case class StateImpl(private val idx: Int, private val ps: List[String]) extends State {
        private def fits(part: String): Boolean     = fitsAt(idx, part, phrase)
        private def collect(p: String): State       = StateImpl(idx + p.length, p :: ps)
        def done: Option[Solution]                  = Option.when(idx == phrase.length)(Solution(ps.reverse))
        def tryCollect(part: String): Option[State] = Option.when(fits(part))(collect(part))
      }
      def init: State = StateImpl(0, Nil)
    }

    val wordsL = LazyList.from(words)

    def go(s: State): LazyList[Solution] = s.done match {
      case Some(s) => LazyList(s)
      case _       =>
        wordsL.flatMap { w =>
          s.tryCollect(w) match {
            case Some(a) => go(a)
            case None    => LazyList.empty
          }
        }
    }

    LazyList
      .from(Option.when(isSolvable(words, phrase))(State.init))
      .flatMap(go)
      .headOption
      .map(_.represent)
  }

  def main(xs: Array[String]): Unit = {
    import scala.io.StdIn.{readLine => nextLine}
    val n = nextLine().toInt
    (1 to n).foreach { _ =>
      val _      = nextLine()
      val words  = nextLine().split(" ")
      val phrase = nextLine()
      val x      = solve(words, phrase).getOrElse("WRONG PASSWORD")
      println(x)
    }
  }

}

class PasswordCrackerFpSpec extends AnyFunSuite with Matchers with ScalaCheckPropertyChecks {
  import PasswordCrackerFp._

  test("fits") {
    val td = List(
      (0, "abc", "abc")  -> true,
      (0, "abc", "abcd") -> true,
      (0, "abd", "abcd") -> false,
      (0, "abc", "ab")   -> false,
      (1, "bc", "abc")   -> true,
      (1, "bcd", "abc")  -> false,
    )

    td.foreach { case (in, out) =>
      (fitsAt _).tupled(in) shouldBe out
    }
  }

  test("easy") {
    val x = solve(
      LazyList("because", "can", "do", "must", "we", "what"),
      "wewewewedowhatwemustbecausewecan"
    )
    x shouldBe Some("we we we we do what we must because we can")
  }

  test("non-solvable shortcut") {
    val x = solve(
      LazyList("a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", "aaaaaaa", "aaaaaaaa", "aaaaaaaaa", "aaaaaaaaaa"),
      "baaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    )
    x shouldBe None
  }

  test("non-solvable") {
    val phrases = Table(
      "unsolvable patterns",
      "baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
      "aaaaaaaaaaaaaaaaaaaaaaaaaabaaaaaaaaaaaaaaaaaaaaaaaa"
    )
    val words   = Iterable("a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", "aaaaaaa", "aaaaaaaa", "aaaaaaaaa", "aaaaaaaaaa")

    forAll(phrases) { phrase =>
      isSolvable(words, phrase) shouldBe false
    }
  }

}
