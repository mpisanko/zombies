package ailo.zombies

sealed trait Direction

/**
 * Direction prefix used to not collide with scala.util.Left
 */
final case object DirectionLeft extends Direction
/**
 * Direction prefix used to not collide with scala.util.Right
 */
final case object DirectionRight extends Direction
/**
 * Direction prefix used for consistency
 */
final case object DirectionUp extends Direction
/**
 * Direction prefix used for consistency
 */
final case object DirectionDown extends Direction

object Direction {
  /**
   * Parse a string into directions ignoring anything else than U/D/L/R characters
   */
  def parse(input: String): List[Direction] =
    Option(input).getOrElse("").trim.toUpperCase.flatMap({
      case 'U' => Some(DirectionUp)
      case 'D' => Some(DirectionDown)
      case 'L' => Some(DirectionLeft)
      case 'R' => Some(DirectionRight)
      case _   => None
    }).toList
}


