package ailo.zombies

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ailo.zombies.Coordinates.BoundedCoordinates

class CoordinatesTest extends AnyFlatSpec with Matchers {

  val bounds: Coordinates = Coordinates(3, 3)
  val initial: BoundedCoordinates = BoundedCoordinates(bounds, Coordinates(1, 1))
  val bounded: (Coordinates => BoundedCoordinates) = BoundedCoordinates(bounds)

  it should "move in the specified direction" in {
    initial.move(DirectionUp) shouldEqual bounded(Coordinates(1, 0))
    initial.move(DirectionDown) shouldEqual bounded(Coordinates(1, 2))
    initial.move(DirectionLeft) shouldEqual bounded(Coordinates(0, 1))
    initial.move(DirectionRight) shouldEqual bounded(Coordinates(2, 1))
  }

  it should "wrap around the bounds" in {
    initial.move(DirectionUp).move(DirectionUp) shouldEqual bounded(Coordinates(1, 2))
    initial.move(DirectionDown).move(DirectionDown) shouldEqual bounded(Coordinates(1, 0))
    initial.move(DirectionLeft).move(DirectionLeft) shouldEqual bounded(Coordinates(2, 1))
    initial.move(DirectionRight).move(DirectionRight) shouldEqual bounded(Coordinates(0, 1))
  }

  it should "return to original position after moving in opposite directions" in {
    initial.move(DirectionLeft).move(DirectionRight) shouldEqual initial
    initial.move(DirectionRight).move(DirectionLeft) shouldEqual initial
    initial.move(DirectionUp).move(DirectionDown) shouldEqual initial
    initial.move(DirectionDown).move(DirectionUp) shouldEqual initial
    initial.move(DirectionLeft).move(DirectionDown).move(DirectionRight).move(DirectionUp) shouldEqual initial
  }

}
