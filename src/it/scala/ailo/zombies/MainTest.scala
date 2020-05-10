package ailo.zombies

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import ailo.zombies.ConfigurationTest.setEnv

class MainTest extends AnyFlatSpecLike with Matchers {

  import scala.util.{Right,Left}

  it should "print help" in {
    assertResult(()) {Main.main(Array("--help"))}
  }

  it should "run zombies with text input" in {
    setEnv(Configuration.INPUT, "sample.txt")
    setEnv(Configuration.OUTPUT, Configuration.JSON)
    Configuration.buildConfiguration(Configuration()) match {
      case Left(err) => fail(s"Error: $err")
      case Right(config) =>
        config.reporter.toString shouldEqual Configuration.JSON
        val world = config.configureWorld().apocalypse
        world.results shouldEqual List(Coordinates(3,0), Coordinates(2,1), Coordinates(1,0), Coordinates(0,0))
    }
  }

  it should "run zombies with json input" in {
    setEnv(Configuration.INPUT, "sample.json")
    setEnv(Configuration.OUTPUT, Configuration.TXT)
    Configuration.buildConfiguration(Configuration()) match {
      case Left(err) => fail(s"Error: $err")
      case Right(config) =>
        config.reporter.toString shouldEqual Configuration.TXT
        val world = config.configureWorld().apocalypse
        world.results shouldEqual List(Coordinates(3,0), Coordinates(2,1), Coordinates(1,0), Coordinates(0,0))
    }
  }

  it should "show error when empty file supplied" in {
    setEnv(Configuration.INPUT, "empty.txt")
    setEnv(Configuration.OUTPUT, Configuration.TXT)
    Configuration.buildConfiguration(Configuration()) match {
      case Left(err) => err shouldEqual "Invalid input: ''"
      case Right(_) => fail("Expected to see error")
    }
  }
}
