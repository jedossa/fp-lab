package types

sealed trait Z
final class _0 extends Z
final class S[A <: Z] extends Z

object Z {
  type _1 = S[_0]
  type _2 = S[_1]
  type _3 = S[_2]
  type _4 = S[_3]
  type _5 = S[_4]
}

trait Sum[A <: Z, B <: Z] { type Out <: Z }

object Sum {
  def apply[A <: Z, B <: Z](implicit sum: Sum[A, B]): Aux[A, B, sum.Out] = sum

  type Aux[A <: Z, B <: Z, C <: Z] = Sum[A, B] { type Out = C }

  implicit def ++[B <: Z]: Aux[_0, B, B] = new Sum[_0, B] { type Out = B }

  implicit def +[A <: Z, B <: Z](implicit sum: Sum[A, S[B]]): Aux[S[A], B, sum.Out] = new Sum[S[A], B] { type Out = sum.Out }
}

object ZMain extends App {
  import Z._
  import Sum._

  val s: Aux[_3, _1, S[S[S[_1]]]] = Sum[_3, _1]
  val x: Aux[_4, _5, S[S[S[S[_5]]]]] = Sum[_4, _5]

}