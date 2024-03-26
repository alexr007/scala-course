package cli.tools

import cats.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.io.File
import scala.sys.process.Process
import scala.util.{Try, Using}

object CloneRepos {

  val home = "/Users/alexr"
  val reposRaw = getClass.getClassLoader.getResource("repos.txt").getFile
  val source = scala.io.Source.fromFile(reposRaw)

  def runCmdIn(cmd: String, cwd: File) = Process(cmd, cwd).!!

  def gitClone(repo: String) = runCmdIn(s"git clone $repo", new File(s"$home/dev/sym"))

  def parseLine(ln: String): Option[String] =
    ln.split(" ")
      .some
      .flatMap(xs => Try(xs(2)).toOption)
      .map(_.replace("bitbucket.org", "bitbucket3.org"))

  def repos = Using(source) { in =>
    in.getLines()
      .flatMap(parseLine)
      .toList
  }
    .fold(throw _, identity)

  repos.map(gitClone)

}

class CloneReposSpec extends AnyFunSuite with Matchers with ScalaCheckPropertyChecks {

  import CloneRepos._

  test("0") {
    repos.map(gitClone)
  }

}
