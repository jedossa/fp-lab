package catsLab

import cats.instances.function._
import cats.syntax.functor._

import scala.math.Numeric.{ DoubleIsFractional, IntIsIntegral }
import scala.math.Ordering.{ DoubleOrdering, IntOrdering }

trait F1Transformer[T] { self: Numeric[T] =>
  type F = (T) => T

  def f: F
  def ht(h: T): F = f map (plus(_, h))
  def vt(k: T): F = f(_) + k
  def hr: F = f map negate
  def vr: F = -f(_)
  def hd(h: T): F = f map (times(_, h))
  def vd(k: T): F = k * f(_)

  def isEven: Boolean = f == hr
  def isOdd: Boolean = f == (hr andThen vr)
}

object F1Transformer {
  implicit class F1Int(fi: Int => Int) extends IntIsIntegral with IntOrdering with F1Transformer[Int] {
    override def f: F = fi
  }

  implicit class F1Double(fd: Double => Double) extends DoubleIsFractional with DoubleOrdering with F1Transformer[Double] {
    override def f: F = fd
  }
}

object FMain extends App {
  import F1Transformer._

  def f: PartialFunction[Int, Int] = { case x if x > 0 => Math.abs(x) }
  def g: Int => Int = identity
  def h: PartialFunction[Double, Double] = { case d if d < 0 => Math.pow(d, 2) }
  def i: PartialFunction[Double, Double] = { case d if d >= 0 => Math.pow(d, 2) }
  def j: Double => Double = h orElse i
  def k: Double => Double = Math.pow(_, 3)

  println(g.isEven)
  println(j.isEven)
  println(k.isOdd)
  println(f.vr(5))
  println(g.hr(-3))
  println(j.vr(2))
  println((j.vr andThen j.vt(-1))(0))
}