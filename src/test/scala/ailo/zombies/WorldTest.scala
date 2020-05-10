package ailo.zombies

import ailo.zombies.Coordinates.BoundedCoordinates
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.Queue

class WorldTest extends AnyFlatSpec with Matchers {

  "awakenZombies" should "take first zombie off the queue and return new zombie and world with one less zombie" in {
    val bounds = Coordinates(3,3)
    val world = World(bounds, Queue(Coordinates(1, 1)), Map.empty, Nil)
    val (newWorld, zombie) = world.awakenZombie
    (newWorld, zombie) shouldEqual
      (World(bounds, Queue.empty, Map.empty, Nil), Some(BoundedCoordinates(bounds, Coordinates(1,1))))
    newWorld.zombies.size shouldBe 0
    val (emptyWorld, noZombie) = newWorld.awakenZombie
    noZombie shouldBe None
    emptyWorld.zombies.size shouldBe 0
  }

  "infectCritters" should "remove critters from zombie position and enqueue them in zombies queue" in {
    val bounds = Coordinates(3,3)
    val world = World(bounds, Queue.empty,
                      Map(Coordinates(1, 1) -> List(Coordinates(1, 1)),
                          Coordinates(2, 2) -> List(Coordinates(2, 2), Coordinates(2, 2), Coordinates(2, 2))), Nil)
    world.infectCritters(Coordinates(2, 2)) shouldEqual
    World(bounds, Queue(Coordinates(2, 2), Coordinates(2, 2), Coordinates(2, 2)),
      Map(Coordinates(1, 1) -> List(Coordinates(1, 1))), Nil)

  }
  it should "not change the world if there are no critters at specified position" in {
    val world = World(Coordinates(3,3), Queue.empty,
      Map(Coordinates(1, 1) -> List(Coordinates(1, 1)),
        Coordinates(2, 2) -> List(Coordinates(2, 2), Coordinates(2, 2), Coordinates(2, 2))), Nil)
    world.infectCritters(Coordinates(0, 0)) shouldEqual world
  }

  "unleashZombie" should "walk the zombie along the path infecting critters on its way, return changed world" in {
    val bounds = Coordinates(3,3)
    val path = List(DirectionRight, DirectionUp, DirectionLeft)
    val world = World(bounds, Queue.empty,
                      Map(Coordinates(1, 1) -> List(Coordinates(1, 1), Coordinates(1, 1)),
                          Coordinates(2, 2) -> List(Coordinates(2, 2))), path)
    world.unleashZombie(BoundedCoordinates(bounds, Coordinates(1, 2))) shouldEqual
      World(bounds, Queue(Coordinates(2, 2), Coordinates(1, 1), Coordinates(1, 1)), Map.empty, path, List(Coordinates(1, 1)))
  }

  it should "teleport zombie to onto opposite side of world's axis if it falls off the ~disk~ square" in {
    val bounds = Coordinates(3,3)
    val path = List(DirectionRight, DirectionRight, DirectionRight)
    val world = World(bounds, Queue.empty,
      Map(Coordinates(1, 2) -> List(Coordinates(1, 2), Coordinates(1, 2)),
        Coordinates(2, 2) -> List(Coordinates(2, 2)), Coordinates(0, 2) -> List(Coordinates(0, 2))), path)
    world.unleashZombie(BoundedCoordinates(bounds, Coordinates(2, 2))) shouldEqual
      World(bounds, Queue(Coordinates(0, 2), Coordinates(1, 2), Coordinates(1, 2), Coordinates(2, 2)),
        Map.empty, path, List(Coordinates(2, 2)))
  }

  it should "not infect creatures that are in the same starting position as zombie" in {
    val bounds = Coordinates(3,3)
    val path = List(DirectionRight, DirectionRight)
    val world = World(bounds, Queue.empty,
      Map(Coordinates(2, 2) -> List(Coordinates(2, 2))), path)
    world.unleashZombie(BoundedCoordinates(bounds, Coordinates(2, 2))) shouldEqual
      World(bounds, Queue.empty, Map(Coordinates(2, 2) -> List(Coordinates(2, 2))), path, List(Coordinates(1, 2)))
  }

  "apocalypse" should "unleash all the zombies and walk each along the path" in {
    val bounds = Coordinates(4, 4)
    val path = List(DirectionDown, DirectionLeft, DirectionUp, DirectionUp, DirectionRight, DirectionRight)
    val world = World(bounds, Queue(Coordinates(2, 1)),
      Map(Coordinates(0,1) -> List(Coordinates(0, 1)),
        Coordinates(1,2) -> List(Coordinates(1,2)),
        Coordinates(3,1) -> List(Coordinates(3,1))),
      path)
      world.apocalypse shouldEqual
        World(bounds, Queue.empty, Map.empty, path,
          List(Coordinates(3,0), Coordinates(2,1), Coordinates(1,0), Coordinates(0,0)))
  }

  it should "not infect any critters when the path is empty (zombie stays put)" in {
    val world = World(Coordinates(66, 67), Queue(Coordinates(2, 1)),
      Map(Coordinates(0,1) -> List(Coordinates(0, 1)),
        Coordinates(1,2) -> List(Coordinates(1,2)),
        Coordinates(3,1) -> List(Coordinates(3,1))),
      Nil)
    world.apocalypse.critters shouldEqual world.critters
  }
}
