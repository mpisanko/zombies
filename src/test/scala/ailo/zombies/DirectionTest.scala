package ailo.zombies

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DirectionTest extends AnyFlatSpec with Matchers {

  it should "parse a null string into empty collection of commands" in {
    Direction.parse(null) shouldEqual Nil
  }
  it should "parse commands from string" in {
    Direction.parse("") shouldEqual List()
    Direction.parse("U") shouldEqual List(DirectionUp)
    Direction.parse("UDLR") shouldEqual List(DirectionUp, DirectionDown, DirectionLeft, DirectionRight)
  }

  it should "parse and ignore invalid commands from string" in {
    Direction.parse("QUWRTYD") shouldEqual List(DirectionUp, DirectionRight, DirectionDown)
  }
}