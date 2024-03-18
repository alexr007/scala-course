package enumeratumx

import cats.implicits.catsSyntaxEitherId
import cats.implicits.catsSyntaxOptionId
import enumeratum.EnumEntry.Lowercase
import enumeratum.EnumEntry.Snakecase
import enumeratum.EnumEntry.Uppercase
import enumeratum._
import io.circe.syntax.EncoderOps
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**  */
object LearnEnumeratum {

  /** 1. extend EnumEntry + naming rule:
    * CapitalSnakecase
    * CapitalHyphencase
    * CapitalDotcase
    * CapitalWords
    *
    * Camelcase
    * Uppercase
    * Lowercase
    * Uncapitalised
    *
    * Snakecase        = CapitalSnakecase  + Lowercase
    * UpperSnakecase   = CapitalSnakecase  + Uppercase
    * Hyphencase       = CapitalHyphencase + Lowercase
    * UpperHyphencase  = CapitalHyphencase + Uppercase
    * Dotcase          = CapitalDotcase    + Lowercase
    * UpperDotcase     = CapitalDotcase    + Uppercase
    * Words            = CapitalWords      + Lowercase
    * UpperWords       = CapitalWords      + Uppercase
    * LowerCamelcase   = Camelcase         + Uncapitalised
    */
  sealed trait Color extends EnumEntry with Lowercase with Snakecase
//    with Uppercase

  /** 2. object extends Enum[A] + needed abilities */
  object Color
      extends Enum[Color]
      with CirceEnum[Color]    // Encoder[A] + Decoder[A]
      with DoobieEnum[Color]   // Meta[A]
      with CatsEnum[Color]     // Show[A] + Hash[A] + Eq[A]
      with ScalacheckInstances // arbEnumEntry[EnumType <: EnumEntry] +
      with CogenInstances      // cogenEnumEntry[EnumType <: EnumEntry: Enum]
      {
    override def values: IndexedSeq[Color] = findValues

    case object Red extends Color {
      override def entryName: String = "rEd"
    }
    case object Green extends Color
  }

}


class LearnEnumeratumSpec extends AnyFunSpec with Matchers {
  import LearnEnumeratum._

  object data {

    private def enquote(x: String) = {
      val q = '"'
      q + x + q
    }

    val c1: Color = Color.Red
    val c2: Color = Color.Green
    val s1        = "rEd"
    val s2        = "green"
    val j1        = enquote(s1)
    val j2        = enquote(s2)
  }

  describe("plain serialization / deserialization to / from String") {

    import data._

    it("serializes to String with help of enumeratum") {
      c1.entryName shouldBe s1
      c2.entryName shouldBe s2
    }

    it("deserializes from String with help of enumeratum") {
      Color.withNameOption(s1) shouldBe c1.some
      Color.withNameOption(s2) shouldBe c2.some
      Color.withNameOption("abracadabra") shouldBe None
    }

  }

  describe("serialization / deserialization with help of circe codec provided by enumeratum") {

    import data._

    it("serializes to String with help of enumeratum") {
      c1.asJson.noSpaces shouldBe j1
      c2.asJson.noSpaces shouldBe j2
    }
    it("deserializes from String with help of enumeratum") {
      io.circe.parser.decode[Color](j1) shouldBe c1.asRight
      io.circe.parser.decode[Color](j2) shouldBe c2.asRight
      io.circe.parser.decode[Color]("abracadabra").isLeft shouldBe true
    }

  }
}
