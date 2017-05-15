package typeClass

trait Transformer[A, B] {
  def transform: (A) => B
}

trait TransformerSyntax {
  implicit class TransformerOps[A](a: A) {
    def transform[B](implicit transformer: Transformer[A, B]): B = transformer.transform(a)
  }
}

trait TransformerInstances extends TransformerSyntax {
  implicit def tuple2Adt1 = new Transformer[(String, Int), ADT1] {
    override def transform: ((String, Int)) => ADT1 = ADT1.tupled
  }

  implicit def dto12Adt1 = new Transformer[DTO1, ADT1] {
    override def transform: (DTO1) => ADT1 = dto => ADT1(dto.e, dto.f)
  }

  implicit def tuple2Adt2 = new Transformer[(String, Int), ADT2] {
    override def transform: ((String, Int)) => ADT2 = ADT2.tupled
  }
}

object transformers extends TransformerInstances

case class ADT1(a: String, b: Int)
case class ADT2(c: String, d: Int)
case class DTO1(e: String, f: Int)

object x {
  import transformers.{ tuple2Adt1, dto12Adt1, TransformerOps }
  val a: ADT1 = ("5", 5).transform
  val b: ADT1 = DTO1("5", 5).transform
  val c: ADT1 = DTO1("5", 5).transform[ADT1]
}

object y {
  import transformers._
  val a: ADT2 = ("5", 5).transform[ADT2]
  val b: ADT1 = ("5", 5).transform[ADT1]
}