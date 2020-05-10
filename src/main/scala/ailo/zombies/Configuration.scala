package ailo.zombies

import ailo.zombies.Configuration.WorldConfiguration

import scala.collection.immutable.Queue
import scala.io.Source
import scala.util.{Either, Left, Right}

case class Configuration(reporter: Reporter = TextReporter(),
                         input: InputParser = TextParser(Source.stdin),
                         world: WorldConfiguration = WorldConfiguration()) {
  def configureWorld(): World =
    World(
      world.bounds,
      Queue[Coordinates](world.zombie),
      world.critters.groupBy(identity),
      world.path)
}

object Configuration {
  val INPUT = "INPUT"
  val TXT = "TXT"
  val JSON = "JSON"
  val OUTPUT = "OUTPUT"
  val DEFAULT_INPUT = TXT

  val buildConfiguration: (Configuration) => Either[String, Configuration] =
    configureReporter _ andThen configureInputSource _ andThen parseWorld _

  def configureReporter(conf: Configuration): Either[String, Configuration] =
    getEnv(OUTPUT, TXT).toUpperCase match {
      case TXT => Right(conf.copy(reporter = TextReporter()))
      case JSON => Right(conf.copy(reporter = JsonReporter()))
      case _ => Left("Unknown output format")
    }

  def configureInputSource(conf: Either[String, Configuration]): Either[String, Configuration] = conf match {
    case Left(err) => Left(err)
    case Right(conf) =>
      InputParser.parserFor(getEnv(INPUT, "")) match {
        case Left(err) => Left(err)
        case Right(parser) => Right(conf.copy(input = parser))
      }
  }

  def parseWorld(conf: Either[String, Configuration]): Either[String, Configuration] = conf match {
    case Left(err) => Left(err)
    case Right(configuration) => configuration.input.parse match {
      case Left(err) => Left(err.mkString("\n"))
      case Right(worldConfig) => Right(configuration.copy(world = worldConfig))
    }
  }

  def getEnv(key: String, default: String): String = {
    scala.util.Properties.envOrElse(key, default)
  }

  def getEnv(key: String): Option[String] = {
    scala.util.Properties.envOrNone(key)
  }

  case class WorldConfiguration(bounds: Coordinates = Coordinates(0, 0),
                                zombie: Coordinates = Coordinates(0, 0),
                                critters: List[Coordinates] = Nil,
                                path: List[Direction] = Nil)
}


