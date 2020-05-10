package ailo.zombies

import scala.util.{Either, Left, Right}

object Main {
  def main(args: Array[String]): Unit = args.toList match {
    case ("--help" :: Nil) => printUsage()
    case _ =>
      runZombies()
      ()
  }

  val USAGE_STRING: String =
    """You can customise the program by using environment variables:
      |INPUT - input file (.txt or .json) to read World definition from (otherwise commands will be read from STDIN in txt format)
      |OUTPUT - output format - one of: txt or json, default txt
      |Both text file and console will have the following format (each of the values on a separate line):
      |
      |Size of world grid, eg: 4
      |Initial position of Zombie, eg: (2,1)
      |List of positions of Critters, eg: (0,1) (1,2) (3,1)
      |Zombie movements/path: (D=down L=left U=up R=right, any other characters will be ignored), eg: DLUURR
      |
      |Json file format will be:
      |{"bounds": 4,
      | "zombie": "(2,1)",
      | "critters": ["(0,1)", "(1,2)", "(3,1)"],
      | "path": "DLUURR"}
      |""".stripMargin

  private def printUsage(): Unit = {
    println(USAGE_STRING)
  }

  def runZombies(): Either[String, World] = Configuration.buildConfiguration(Configuration()) match {
    case Right(configuration) =>
      val world = configuration.configureWorld
      val postApocalypse = world.apocalypse
      println(configuration.reporter.output(postApocalypse.results))
      Right(postApocalypse)
    case Left(err) =>
      println(err)
      println("\n\nRun the programme with '--help' to see help.")
      Left(err)
  }
}
