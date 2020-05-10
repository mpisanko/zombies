lazy val IntegrationTest = config("it") extend(Test)
lazy val circeVersion = "0.12.3"
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(
    inThisBuild(List(
      organization := "ailo.zombies",
      scalaVersion := "2.13.2",
      version := "0.0.1",
      assemblyJarName in assembly := "zombies.jar"
    )),
    name := "zombies",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "it,test",
    Defaults.itSettings
  )
