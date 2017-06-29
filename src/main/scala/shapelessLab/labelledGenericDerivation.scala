package shapelessLab

import java.util.Date

import cats.{ Foldable, Show }
import shapeless.{ ::, HList, HNil, LabelledProductTypeClass, LabelledProductTypeClassCompanion }

trait CSV[A] {
  def to: (A) => String
}

object CSV {
  def to[A](a: A)(implicit c: CSV[A]): String = c to a

  def apply[A](f: (A) => String): CSV[A] = new CSV[A] {
    override def to: (A) => String = f
  }
}

object CSVs extends LabelledProductTypeClassCompanion[CSV] {
  override val typeClass: LabelledProductTypeClass[CSV] = new LabelledProductTypeClass[CSV] {
    override def product[H, T <: HList](name: String, ch: CSV[H], ct: CSV[T]): CSV[::[H, T]] = CSV[H :: T] {
      case h :: HNil => s"$name: ${ch.to(h)}"
      case h :: t =>
        s"$name: ${ch.to(h)}, ${ct.to(t)}"
    }

    override def emptyProduct: CSV[HNil] = CSV[HNil](_ => "")

    override def project[F, G](instance: => CSV[G], to: (F) => G, from: (G) => F): CSV[F] = CSV[F](instance.to compose to)
  }

  implicit def showCSV[A](implicit s: Show[A]): CSV[A] = CSV[A](s.show)

  implicit def optionCSV[A](implicit c: CSV[A]): CSV[Option[A]] = CSV[Option[A]](_.fold("null")(c.to))

  implicit def foldCSV[A, F[_]](implicit c: CSV[A], f: Foldable[F]): CSV[F[A]] = CSV[F[A]](f.foldLeft(_, "")((a, b) => s"[${a ++ c.to(b)}]"))
}

case class LADT1(s: String = "1", d: Option[Double] = Some(1.1D), e: Option[Double] = None)
case class LADT11(s: String = "11", d: Option[Double] = None, i: Int = 11, l: List[LADT3] = List(LADT3(), LADT3()))
case class LADT2(s: String = "2", x: String = "2", i: Option[Date] = None)
case class LADT3(s: String = "3", i: Int = 3, d: Double = 3D, date: Date = new Date())

object LMain extends App {
  import CSVs._
  import cats.instances.all._

  implicit def Date2Show: Show[Date] = Show.fromToString[Date]

  val p1: String = CSV.to(LADT1())
  val p11 = CSV.to(LADT11())
  val p2 = CSV.to(LADT2())
  val p3 = CSV.to(LADT3())
  val p4 = CSV.to(LADT3())

  println(p1)
  println(p11)
  println(p2)
  println(p3)
  println(p4)
}