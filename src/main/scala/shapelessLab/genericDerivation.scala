package shapelessLab

import java.util.Date

import cats.syntax.either._
import cats.syntax.option._
import shapeless.{ ::, HList, HNil, ProductTypeClass, ProductTypeClassCompanion }

trait Parser[A] {
  def parse: (String) => ReadError Either A
}

object Parser {
  def parse[A](s: String)(implicit p: Parser[A]): Either[ReadError, A] = p parse s

  def apply[A](f: (String) => Either[ReadError, A]): Parser[A] = new Parser[A] {
    override def parse: (String) => Either[ReadError, A] = f
  }
}

object Parsers extends ProductTypeClassCompanion[Parser] {
  override val typeClass: ProductTypeClass[Parser] = new ProductTypeClass[Parser] {
    override def product[H, T <: HList](ch: Parser[H], ct: Parser[T]): Parser[H :: T] = Parser[H :: T](
      _.trim.split(",").toList match {
        case h +: t => for {
          head <- ch.parse(h)
          tail <- ct.parse(t.mkString(","))
        } yield head :: tail
        case _ => EmptyError().asLeft
      }
    )

    override def project[F, G](instance: => Parser[G], to: (F) => G, from: (G) => F): Parser[F] = Parser[F](
      instance.parse(_) map from
    )

    override def emptyProduct: Parser[HNil] = Parser[HNil](s => if (s.isEmpty) HNil.asRight else EmptyError().asLeft)
  }

  implicit def readParser[A](implicit r: Read[A]): Parser[A] = Parser[A](r.reads)

  implicit def optionParser[A](implicit p: Parser[A]): Parser[Option[A]] = Parser[Option[A]] { s =>
    if (s.isEmpty) none.asRight else p.parse(s) map (_.some)
  }
}

case class ADT1(s: String, d: Option[Double], e: Option[Double])
case class ADT11(s: String, d: Option[Double], i: Int)
case class ADT2(s: String, x: String, i: Option[Date])
case class ADT3(s: String, i: Int, d: Double, date: Date)

object Main extends App {
  import Parsers._

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