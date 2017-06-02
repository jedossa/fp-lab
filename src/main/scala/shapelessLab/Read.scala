package shapelessLab

import java.text.SimpleDateFormat
import java.util.Date

import cats.syntax.either._

import scala.util.Try

trait Read[A] {
  def reads(s: String): ReadError Either A
}

trait ReadInstances {
  implicit def stringReads: Read[String] = Read(_.asRight)

  implicit def intReads: Read[Int] = Read(s => Try(s.toInt).toEither.leftMap(_ => IntError()))

  implicit def doubleReads: Read[Double] = Read(s => Try(s.toDouble).toEither.leftMap(_ => DoubleError()))

  implicit def dateReads: Read[Date] = Read { s =>
    val formatter = new SimpleDateFormat("dd-MM-yyyyy")
    Try(formatter.parse(s)).toEither.leftMap(_ => DateError())
  }
}

object Read extends ReadInstances {
  def apply[A](f: (String) => Either[ReadError, A]): Read[A] = f(_)

  def reads[A](s: String)(implicit r: Read[A]): Either[ReadError, A] = r reads s
}

trait ReadError {
  def msg: String
}

case class EmptyError(msg: String = "Field Empty") extends ReadError
case class IntError(msg: String = "Not an Int") extends ReadError
case class DoubleError(msg: String = "Not a Double") extends ReadError
case class DateError(msg: String = "Not a Date") extends ReadError