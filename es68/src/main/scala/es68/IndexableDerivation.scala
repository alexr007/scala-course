package es68

import com.sksamuel.elastic4s.{AggReader, Hit, HitReader, Indexable}
import io.circe.{Decoder, Encoder, Json, Printer}
import io.circe.jawn.decode

import scala.annotation.implicitNotFound
import scala.util.{Failure, Success, Try}

/**
  * this is just copy-paste of
  * "elastic4s-circe" to fix dependency on "circe-core":"0.12.0-M3"
  */
object IndexableDerivation {

  @implicitNotFound(
    "No Decoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Decoder instance "
  )
  implicit def hitReaderWithCirce[T](implicit decoder: Decoder[T]): HitReader[T] = new HitReader[T] {
    override def read(hit: Hit): Try[T] = decode[T](hit.sourceAsString).fold(Failure(_), Success(_))
  }

  @implicitNotFound(
    "No Encoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Encoder instance "
  )
  implicit def indexableWithCirce[T](implicit encoder: Encoder[T],
                                     printer: Json => String = Printer.noSpaces.print): Indexable[T] =
    new Indexable[T] {
      override def json(t: T): String = printer(encoder(t))
    }

  @implicitNotFound(
    "No Decoder for type ${T} found. Use 'import io.circe.generic.auto._' or provide an implicit Decoder instance "
  )
  implicit def aggReaderWithCirce[T](implicit encoder: Decoder[T]): AggReader[T] = new AggReader[T] {
    override def read(json: String): Try[T] = decode[T](json).fold(Failure(_), Success(_))
  }
}


