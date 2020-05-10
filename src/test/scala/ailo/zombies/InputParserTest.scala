package ailo.zombies

import ailo.zombies.Configuration.WorldConfiguration
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.List
import scala.io.Source
import scala.util.{Left, Right}

class InputParserTest extends AnyFlatSpec with Matchers {

  "parserFor" should "create TextParser with stdin as input for empty string" in {
    InputParser.parserFor("").getClass shouldBe Right(TextParser(Source.stdin)).getClass
  }

  it should "create TextParser for a .txt file" in {
    InputParser.parserFor("sample.txt").getClass shouldEqual
      Right(TextParser(Source.fromFile("sample.txt"))).getClass
  }

  it should "create JsonParser for a .json file" in {
    InputParser.parserFor("sample.json").getClass shouldEqual
      Right(JsonParser(Source.fromFile("sample.json"))).getClass
  }

  "bounds" should "return Left for invalid input" in {
    InputParser.bounds("") shouldEqual Left("Specified bounds invalid ")
    InputParser.bounds("foo") shouldEqual Left("Specified bounds invalid foo")
    InputParser.bounds("0") shouldEqual Left("Specified bounds invalid 0")
    InputParser.bounds("-12") shouldEqual Left("Specified bounds invalid -12")
  }

  it should "return Right for valid input" in {
    InputParser.bounds("1") shouldEqual Right(Coordinates(1, 1))
    InputParser.bounds("666") shouldEqual Right(Coordinates(666, 666))
  }

  "parseCoordinates" should "return Left for invalid input" in {
    InputParser.coordinates("1,1") shouldEqual Left("Malformed coordinates 1,1")
    InputParser.coordinates("(1,1") shouldEqual Left("Malformed coordinates (1,1")
    InputParser.coordinates("1,1)") shouldEqual Left("Malformed coordinates 1,1)")
    InputParser.coordinates("(-1,1)") shouldEqual Left("Malformed coordinates (-1,1)")
    InputParser.coordinates("(1 1)") shouldEqual Left("Malformed coordinates (1 1)")
    InputParser.coordinates("(foo,bar)") shouldEqual Left("Malformed coordinates (foo,bar)")
  }

  it should "return Right for valid input" in {
    InputParser.coordinates("(1,1)") shouldEqual Right(Coordinates(1, 1))
    InputParser.coordinates("( 1, 1)") shouldEqual Right(Coordinates(1, 1))
    InputParser.coordinates("( 1 , 1 )") shouldEqual Right(Coordinates(1, 1))
    InputParser.coordinates("( 667 , 666 )") shouldEqual Right(Coordinates(667, 666))
  }

  "multiCoordinates" should "return errors when invalid coordinates specified" in {
    InputParser.multiCoordinates("(1,1)(foo,2)qw,)") shouldEqual
      Left(List("Malformed coordinates (foo,2)"))
    InputParser.multiCoordinates("(-11,1)(34,2)") shouldEqual
      Left(List("Malformed coordinates (-11,1)"))
    InputParser.multiCoordinates("(-11,1)(34,baz)") shouldEqual
      Left(List("Malformed coordinates (-11,1)",
        "Malformed coordinates (34,baz)"))
  }

  it should "return a Right with parsed coordinates list when successful" in {
    InputParser.multiCoordinates("(1,1)(3,2)(4,8))") shouldEqual
      Right(List(Coordinates(1, 1), Coordinates(3, 2), Coordinates(4, 8)))
    InputParser.multiCoordinates("(1,0)(0,0)(1,0)(0,2))") shouldEqual
      Right(List(Coordinates(1, 0), Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 2)))
  }

  it should "return an empty Right when no coordinates specified" in {
    InputParser.multiCoordinates("") shouldEqual Right(Nil)
  }

  "configuration" should "parse all passed in values and return Right of WorldConfiguration when successful" in {
    InputParser.configuration(
      Map("bounds" -> "5", "zombie" -> "(1,1)",
        "critters" -> "(1,2)(2,2)(3,2)(4,2)", "path" -> "DRRRRU")) shouldEqual
      Right(WorldConfiguration(Coordinates(5, 5), Coordinates(1, 1),
        List(Coordinates(1, 2), Coordinates(2, 2), Coordinates(3, 2), Coordinates(4, 2)),
        List(DirectionDown, DirectionRight, DirectionRight, DirectionRight, DirectionRight, DirectionUp)))
  }

  it should "return Left with errors when there are any problems" in {
    InputParser.configuration(
      Map("bounds" -> "0", "zombie" -> "(foo,1)",
        "critters" -> "(1,2)(2,2)(3,2)(4,2)", "path" -> "")) shouldEqual
      Left(List("Specified bounds invalid 0",
        "Malformed coordinates (foo,1)"))

    InputParser.configuration(
      Map("bounds" -> "5", "zombie" -> "(5,1)",
        "critters" -> "(1,2)(2,2)(3,2)(4,5)", "path" -> "DRRRRU")) shouldEqual
      Left(List("Coordinate (5,1) out of bounds: (5,5)",
        "Coordinate (4,5) out of bounds: (5,5)"))
  }

  "JsonParser" should "parse JSON contents" in {
    JsonParser(
      Source.fromString(
        """{"bounds": 4,
          | "zombie": "(2,1)",
          | "critters": ["(0,1)", "(1,2)", "(3,1)"],
          | "path": "DLUURR"}""".stripMargin)).parse shouldEqual
      Right(WorldConfiguration(Coordinates(4, 4), Coordinates(2, 1),
        List(Coordinates(0, 1), Coordinates(1, 2), Coordinates(3, 1)),
        List(DirectionDown, DirectionLeft, DirectionUp, DirectionUp, DirectionRight, DirectionRight)))
  }

  it should "parse JSON contents with empty critters" in {
    JsonParser(
      Source.fromString(
        """{"bounds": 4,
          | "zombie": "(2,1)",
          | "critters": [],
          | "path": "DLUURR"}""".stripMargin)).parse shouldEqual
      Right(WorldConfiguration(Coordinates(4, 4), Coordinates(2, 1),
        Nil, List(DirectionDown, DirectionLeft, DirectionUp, DirectionUp, DirectionRight, DirectionRight)))
  }

  it should "return Left when incomplete JSON contents supplied" in {
    JsonParser(
      Source.fromString(
        """{"bounds": 4,
          | "zombie": "(2,1)",
          | "path": "DLUURR"}""".stripMargin)).parse shouldEqual
      Left(List(
        """Invalid input: '{"bounds": 4,
          | "zombie": "(2,1)",
          | "path": "DLUURR"}'""".stripMargin))
  }

  "TextParser" should "parse text content" in {
    TextParser(
      Source.fromString(
        """4
          |(2,1)
          |(0,1) (1,2) (3,1)
          |DLUURR
          |""".stripMargin)).parse shouldEqual
      Right(WorldConfiguration(Coordinates(4, 4), Coordinates(2, 1),
        List(Coordinates(0, 1), Coordinates(1, 2), Coordinates(3, 1)),
        List(DirectionDown, DirectionLeft, DirectionUp, DirectionUp, DirectionRight, DirectionRight)))
  }
}
