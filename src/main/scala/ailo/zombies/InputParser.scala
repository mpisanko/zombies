package ailo.zombies

import java.io.File

import ailo.zombies.Configuration.WorldConfiguration
import io.circe.Decoder
import io.circe.parser.decode

import scala.io.Source
import scala.util.matching.Regex.Match
import scala.util.{Either, Left, Right}

sealed trait InputParser {
  def parse(): Either[List[String], WorldConfiguration]
}

object InputParser {
  def parserFor(filename: String): Either[String, InputParser] = filename match {
    case "" => {
      println("Please provide:\nworld size\nzombie position\npositions of creatures\nlist of movements\neach on separate line, then press CRTL+D")
      Right(TextParser(Source.stdin))
    }
    case fn if (new File(fn).exists && fn.endsWith(".json")) => Right(JsonParser(Source.fromFile(fn)))
    case fn if (!fn.endsWith(".txt")) => Left(s"File format not supported: $fn")
    case fn if (!new File(fn).exists) => Left("Supplied Input file does not exist")
    case _ => Right(TextParser(Source.fromFile(filename)))
  }

  def bounds(bounds: String): Either[String, Coordinates] = bounds.trim.toIntOption match {
    case Some(b) if b > 0 => Right(Coordinates(b, b))
    case _ => Left(s"Specified bounds invalid $bounds")
  }

  val coordinatesPattern = """\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)\s*""".r
  def coordinates(coordinates: String): Either[String, Coordinates] = coordinates match {
    case coordinatesPattern(x, y) => Right(Coordinates(x.toInt, y.toInt))
    case _ => Left(s"Malformed coordinates $coordinates")
  }

  def coordinatesFromPair(matchGroup: Match): Either[String, Coordinates] =
    (matchGroup.group(1).toIntOption, matchGroup.group(2).toIntOption) match {
      case (Some(x), Some(y)) if x >= 0 && y >= 0 => Right(Coordinates(x, y))
      case _ => Left(s"Malformed coordinates ${matchGroup.matched}")
    }

  /**
   * Given that there are many coordinates - do not restrict matches to integers to be able to detect errors
   */
  val multiCoordinatesPattern = """\s*\(\s*([^,)]+)\s*,\s*([^)]+)\s*\)\s*""".r
  def multiCoordinates(multiCoordinates: String): Either[List[String], List[Coordinates]] =
    multiCoordinatesPattern.findAllMatchIn(multiCoordinates).toList.partitionMap(coordinatesFromPair) match {
      case (Nil, listOfRights) => Right(listOfRights)
      case (listOfLefts, _)    => Left(listOfLefts)
    }

  def configuration(input: Map[String, String]): Either[List[String], WorldConfiguration] = {
    val bounds = InputParser.bounds(input.getOrElse("bounds", ""))
    val zombie = InputParser.coordinates(input.getOrElse("zombie", ""))
    val critters = InputParser.multiCoordinates(input.getOrElse("critters", ""))
    (bounds, zombie, critters) match {
      case (Right(b), Right(z), Right(cs)) =>
        (z :: cs).partition(_.within(b)) match {
          case (_, Nil) =>
            Right(WorldConfiguration(b, z, cs, Direction.parse(input.getOrElse("path", ""))))
          case (_, outOfBounds) =>
            Left(outOfBounds.map(c => s"Coordinate ${c.toString} out of bounds: ${b.toString}"))
        }
      case (_, _, Left(cs)) => Left(List(bounds, zombie).partitionMap(identity)._1.concat(cs))
      case (_, _, Right(_)) => Left(List(bounds, zombie).partitionMap(identity)._1)
    }
  }
}

/**
 * Text file or console input will have pure unadorned values separated by newlines
 * @param source
 */
final case class TextParser(source: Source) extends InputParser {
  override def parse(): Either[List[String], WorldConfiguration] = source.getLines().toList match {
    case bounds :: zombie :: critters :: path :: _ =>
      InputParser.configuration(
        Map("bounds"   -> bounds,
            "zombie"   -> zombie,
            "critters" -> critters,
            "path"     -> path))
    case _ => Left(List(s"Invalid input: '${source.getLines().toList.mkString("\n")}'"))
  }
}

/**
 * JSON file input will have the following structure:
 * {"bounds": 4,
 * "zombie": "(2,1)",
 * "critters": ["(0,1)", "(1,2)", "(3,1)"],
 * "path": "DLUURR"}
 *
 * @param source
 */
final case class JsonParser(source: Source) extends InputParser {
  override def parse(): Either[List[String], WorldConfiguration] = {
    val input = source.getLines.toList
    decode[JsonParser.JsonConfig](input.mkString) match {
      case Right(jc) => InputParser.configuration(jc.asMap)
      case _ => Left(List(s"Invalid input: '${input.mkString("\n")}'"))
    }
  }
}

object JsonParser {
  case class JsonConfig(bounds: Int, zombie: String, critters: List[String], path: String) {
    def asMap: Map[String, String] =
      Map("bounds" -> bounds.toString,
          "zombie" -> zombie,
          "critters" -> critters.mkString,
          "path" -> path)
  }
  implicit val decodeJsonConfig: Decoder[JsonConfig] =
    Decoder.forProduct4("bounds", "zombie", "critters", "path")(JsonConfig.apply)
}