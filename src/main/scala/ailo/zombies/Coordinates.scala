package ailo.zombies

/**
 * Zero based Coordinates
 */
case class Coordinates(x: Int, y: Int) {
  def within(bounds: Coordinates): Boolean = x < bounds.x && y < bounds.y
  override def toString: String = s"($x,$y)"
}

object Coordinates {

  /**
   * Coordinates within a grid constrained by bounds - also store the bounds (Sizes of the grid)
   * @param bounds Sizes of the grid - exclusive (given it's zero based index)
   * @param position Position within the bounds
   */
  case class BoundedCoordinates(bounds: Coordinates, position: Coordinates) {
    /**
     * Move to coordinates representing position after a move in given direction, wrapping around at bounds.
     * x is horizontal axis, y is vertical axis, (0,0) representing top left corner
     * @param direction direction of movement
     * @return new BoundedCoordinate after move (bounded by the original bounds)
     */
    def move(direction: Direction): BoundedCoordinates = direction match {
      case DirectionUp => this.copy(position = position.copy(y = wrapAround(position.y - 1, bounds.y)))
      case DirectionDown => this.copy(position = position.copy(y = wrapAround(position.y + 1, bounds.y)))
      case DirectionRight => this.copy(position = position.copy(x = wrapAround(position.x + 1, bounds.x)))
      case DirectionLeft => this.copy(position = position.copy(x = wrapAround(position.x - 1, bounds.x)))
    }

    private def wrapAround(newCoordinate: Int, boundingCoordinate: Int): Int = newCoordinate match {
      case c if c < 0 => boundingCoordinate - 1
      case _          => newCoordinate % boundingCoordinate
    }
  }

  object BoundedCoordinates {
    def apply(bounds: Coordinates): (Coordinates => BoundedCoordinates) = BoundedCoordinates(bounds, _)
  }
}

