package ailo.zombies

sealed trait Reporter {
  def output(result: List[Coordinates]): String
  override def toString: String = super.toString
}

final case class TextReporter() extends Reporter {
  override def output(result: List[Coordinates]): String =
    s"""zombies score: ${result.size - 1}
       |zombies positions:
       |${result.map(_.toString).mkString}
       |""".stripMargin

  override def toString: String = "TXT"
}

final case class JsonReporter() extends Reporter {
  override def output(result: List[Coordinates]): String = {
    val coords = result.map(c => s""""${c.toString}"""").mkString(",")
    s"""{
       |"zombies-score": ${result.size - 1},
       |"zombies-positions": [$coords]
       |}""".stripMargin
  }
  override def toString: String = "JSON"
}