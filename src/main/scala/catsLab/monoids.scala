package catsLab

import cats.Monoid

trait R2[A] {
  type P = A Tuple2 A
  type I = (A) => A
  type ∆ = (P) => P

  def Ø: P
  def → : ∆
  def ↤ : ∆
  def ↓ : ∆
  def ↑ : ∆
  def ↗ : ∆
  def ↘ : ∆
  def ↖ : ∆
  def ↙ : ∆
  def ⇌ : ∆

  def δ(f: I)(g: I): ∆ = { case (x, y) => f(x) -> g(y) }

  implicit def rm: Monoid[∆] = new Monoid[∆] {
    override def empty: ∆ = ⇌
    override def combine(f: ∆, g: ∆): ∆ = f andThen g
  }
}

case object ℤ extends R2[Int] {
  override def Ø: P = (0, 0)
  override def → : ∆ = δ(_ + 1)(identity)
  override def ↤ : ∆ = δ(_ - 1)(identity)
  override def ↓ : ∆ = δ(identity)(_ - 1)
  override def ↑ : ∆ = δ(identity)(_ + 1)
  override def ↗ : ∆ = δ(_ + 1)(_ + 1)
  override def ↘ : ∆ = δ(_ + 1)(_ - 1)
  override def ↖ : ∆ = δ(_ - 1)(_ + 1)
  override def ↙ : ∆ = δ(_ - 1)(_ - 1)
  override def ⇌ : ∆ = δ(identity)(identity)
}

object Main extends App {
  import ℤ._

  val ms = List(→,⇌,↓,⇌,↓,↗,→,↤,↙,↖,↤,↗)
  val m: ∆ = Monoid[∆].combineAll(ms)
  val r: P = m(Ø)

  println(r)
}