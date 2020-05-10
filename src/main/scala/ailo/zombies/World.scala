package ailo.zombies


import scala.collection.immutable.Queue

/**
 * Representation of Apocalyptic Zombie World
 * @param bounds boundaries of the World (they're actually right outside of World)
 * @param zombies dormant Zombies
 * @param critters Map of World positions occupied by Critters
 * @param path List of Directions Zombie will move in
 * @param results final positions of Zombies (after they've been unleashed)
 */
case class World(bounds: Coordinates,
                 zombies: Queue[Coordinates],
                 critters: Map[Coordinates, List[Coordinates]],
                 path: List[Direction],
                 results: List[Coordinates] = Nil) {

  import ailo.zombies.World.Zombie

  /**
   * Infect Critters located in position
   * @param position
   * @return new World with more dormant Zombies and less Critter
   */
  def infectCritters(position: Coordinates): World =
    copy(zombies = zombies.enqueueAll(critters.getOrElse(position, Nil)),
      critters = critters - position)

  def awakenZombie(): (World, Option[Zombie]) = zombies.dequeueOption match {
    case Some((z, rest)) => (copy(zombies = rest), Some(World.createZombie(bounds, z)))
    case None            => (this, None)
  }

  /**
   * Awaken Zombie and let it do its dance along given path
   * @return a pair of new World after Zombie has moved and the exhausted Zombie
   */
  def unleashZombie(zombie: Zombie): World = {
    val (exhaustedZombie, newWorld) = path.foldLeft((zombie, this)) { (zombieWorld, direction) =>
        val (zombie, world) = zombieWorld
        val movedZombie = zombie.move(direction)
        (movedZombie, world.infectCritters(movedZombie.position))
      }
    newWorld.copy(results = results.appended(exhaustedZombie.position))
  }

  def apocalypse(): World = awakenZombie match {
    case (world, Some(zombie)) =>
      world.unleashZombie(zombie).apocalypse()
    case (world, None) => world
  }
}

object World {
  import ailo.zombies.Coordinates.BoundedCoordinates
  /**
   * Awakened Zombie
   */
  type Zombie = BoundedCoordinates
  def createZombie(bounds: Coordinates, position: Coordinates): Zombie = BoundedCoordinates(bounds, position)
}
