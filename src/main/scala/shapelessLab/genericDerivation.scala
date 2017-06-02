package shapelessLab

import java.util.Date

import cats.syntax.either._
import cats.syntax.option._
import shapeless.{ ::, Generic, HList, HNil, the }

trait Parser[A] {
  def parse: (String) => ReadError Either A
}

object Parser {
  def parse[A](s: String)(implicit p: Parser[A]): Either[ReadError, A] = p parse s

  def apply[A](f: (String) => Either[ReadError, A]): Parser[A] = new Parser[A] {
    override def parse: (String) => Either[ReadError, A] = f
  }
}

trait ParserInstances {
  implicit def readParser[A](implicit r: Read[A]): Parser[A] = Parser[A](r.reads)

  implicit def optionParser[A](implicit p: Parser[A]): Parser[Option[A]] = Parser[Option[A]] { s =>
    if (s.isEmpty) none.asRight else p.parse(s) map (_.some)
  }

  implicit def hnilParser: Parser[HNil] = Parser[HNil](s => if (s.isEmpty) HNil.asRight else EmptyError().asLeft)

  implicit def hconsParser[H: Parser, T <: HList: Parser]: Parser[H :: T] = Parser[H :: T](
    _.trim.split(",").toList match {
      case h +: t => for {
        head <- the[Parser[H]].parse(h)
        tail <- the[Parser[T]].parse(t.mkString(","))
      } yield head :: tail
      case _ => EmptyError().asLeft
    }
  )

  implicit def adtParser[A, R <: HList](implicit gen: Generic.Aux[A, R], reprParser: Parser[R]): Parser[A] = Parser[A](
    reprParser.parse(_).map(gen.from)
  )
}

object parsers extends ParserInstances

case class ADT1(s: String, d: Option[Double], e: Option[Double])
case class ADT11(s: String, d: Option[Double], i: Int)
case class ADT2(s: String, x: String, i: Option[Date])
case class ADT3(s: String, i: Int, d: Double, date: Date)

object Main extends App {
  import parsers._

  val p1: Either[ReadError, ADT1] = Parser.parse[ADT1]("1, 1.1")
  val p11 = Parser.parse[ADT11]("1, , 1")
  val p2 = Parser.parse[ADT2]("2, 2, now")
  val p3 = Parser.parse[ADT3]("3, q, 3.1, 11-12-2015")
  val p4 = Parser.parse[ADT3]("3, 3, 3, 10-07-2001")

  println(p1)
  println(p11)
  println(p2)
  println(p3)
  println(p4)
}