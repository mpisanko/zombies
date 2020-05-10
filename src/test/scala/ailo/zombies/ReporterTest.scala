package ailo.zombies

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReporterTest extends AnyFlatSpec with Matchers {
  "TextReporter" should "output results in text format" in {
    TextReporter()
      .output(List(Coordinates(0,1), Coordinates(1,1), Coordinates(2,1))) shouldEqual
    """zombies score: 2
      |zombies positions:
      |(0,1)(1,1)(2,1)
      |""".stripMargin
  }

  "JsonReporter" should "output results in json format" in {
    JsonReporter()
      .output(List(Coordinates(0,1), Coordinates(1,1), Coordinates(2,1))).stripMargin shouldEqual
    """{
        |"zombies-score": 2,
        |"zombies-positions": ["(0,1)","(1,1)","(2,1)"]
        |}""".stripMargin
  }
}
