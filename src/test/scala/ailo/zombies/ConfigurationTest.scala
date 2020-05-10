package ailo.zombies

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class ConfigurationTest extends AnyFlatSpec with Matchers {
  "configureReporter" should "check the environment variable to check the format" in {
    ConfigurationTest.setEnv(Configuration.OUTPUT, Configuration.TXT)
    Configuration.configureReporter(Configuration()) match {
      case Left(err) => fail(s"Failed with error: $err")
      case Right(conf) => conf.reporter.toString shouldEqual Configuration.TXT
    }
  }
  it should "return text reporter if OUTPUT not set" in {
    ConfigurationTest.rmEnv(Configuration.OUTPUT)
    Configuration.configureReporter(Configuration()) match {
      case Left(err) => fail(s"Failed with error: $err")
      case Right(conf) => conf.reporter.toString shouldEqual Configuration.TXT
    }
  }
  it should "return JSON reporter when OUTPUT set to JSON" in {
    ConfigurationTest.setEnv(Configuration.OUTPUT, Configuration.JSON)
    Configuration.configureReporter(Configuration()) match {
      case Left(err) => fail(s"Failed with error: $err")
      case Right(conf) => conf.reporter.toString shouldEqual Configuration.JSON
    }
  }

  it should "return an error when unknown OUTPUT set" in {
    ConfigurationTest.setEnv(Configuration.OUTPUT, "FOOBAR")
    Configuration.configureReporter(Configuration()) match {
      case Left(err) => err shouldBe "Unknown output format"
      case Right(conf) => fail("Should not succeed with format FOOBAR")
    }
  }

  "configureInputSource" should "return error if error had been encountered before" in {
    Configuration.configureInputSource(Left("error")) shouldEqual Left("error")
  }

  it should "use parser based on INPUT file extension" in {
    ConfigurationTest.setEnv(Configuration.INPUT, "sample.txt")
    Configuration.configureInputSource(Right(Configuration())) match {
      case Left(err) => fail(s"Should not fail, got error: $err")
      case Right(conf) => conf.input.getClass shouldEqual TextParser(Source.fromFile("sample.txt")).getClass
    }
  }

  it should "use JSON parser when INPUT specified to JSON file" in {
    ConfigurationTest.setEnv(Configuration.INPUT, "sample.json")
    Configuration.configureInputSource(Right(Configuration())) match {
      case Left(err) => fail(s"Should not fail, got error: $err")
      case Right(conf) => conf.input.getClass shouldEqual JsonParser(Source.fromFile("sample.json")).getClass
    }
  }

  it should "use Text parser when no INPUT was specified" in {
    ConfigurationTest.rmEnv(Configuration.INPUT)
    Configuration.configureInputSource(Right(Configuration())) match {
      case Left(err) => fail(s"Should not fail, got error: $err")
      case Right(conf) => conf.input.getClass shouldEqual TextParser(Source.stdin).getClass
    }
  }

  it should "return error for file which does not exist" in {
    ConfigurationTest.setEnv(Configuration.INPUT, "/dev/null/not-there-for-sure.txt")
    Configuration.configureInputSource(Right(Configuration())) match {
      case Left(err) => err shouldEqual "Supplied Input file does not exist"
      case Right(_) => fail("Should have failed")
    }
  }

  it should "return error for unsupported file format" in {
    ConfigurationTest.setEnv(Configuration.INPUT, "sample.xml")
    Configuration.configureInputSource(Right(Configuration())) match {
      case Left(err) => err shouldEqual "File format not supported: sample.xml"
      case Right(_) => fail("Should have failed")
    }
  }
}

object ConfigurationTest {
  def setEnv(key: String, value: String) = {
    enabledEnvMap.put(key, value)
  }
  def rmEnv(key: String) = {
    enabledEnvMap.remove(key)
  }

  private def enabledEnvMap: java.util.Map[String, String] = {
    val field = System.getenv().getClass.getDeclaredField("m")
    field.setAccessible(true)
    field.get(System.getenv()).asInstanceOf[java.util.Map[java.lang.String, java.lang.String]]
  }
}