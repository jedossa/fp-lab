package types

sealed trait Bool {
  type If[T <: Bool, F <: Bool] <: Bool
}
final class True extends Bool {
  type If[T <: Bool, F <: Bool] = T
}
final class False extends Bool {
  type If[T <: Bool, F <: Bool] = F
}

object Bool {
  type &&[A <: Bool, B <: Bool] = A#If[B, False]
  type ||[A <: Bool, B <: Bool] = A#If[True, B]
  type Not[A <: Bool] = A#If[False, True]

  final case class BoolRep[B <: Bool](value: Boolean)

  implicit val falseRep: BoolRep[False] = BoolRep(false)
  implicit val trueRep: BoolRep[True] = BoolRep(true)

  def toBoolean[B <: Bool](implicit b: BoolRep[B]): Boolean = b.value
}

object BMain extends App {
  import Bool._

  val b: Boolean = toBoolean[True && False || Not[False]]

  println(b)

}