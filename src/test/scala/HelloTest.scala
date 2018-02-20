import org.scalatest._

class HelloTest extends FreeSpec {
  "Say hello" in {
    lazy val hello = "hello"
    assert(hello == "hello")
  }
}
