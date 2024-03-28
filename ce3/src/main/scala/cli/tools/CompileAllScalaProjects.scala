package cli.tools

import cats.effect.IO
import cats.effect.IOApp
import cats.implicits._

import java.io.File
import scala.sys.process.Process

object CompileAllScalaProjects extends IOApp.Simple {

  val root = "/Users/alexr/dev/sym"

  def detectScalaRepo(dir: File) =
    dir
      .listFiles()
      .find(_.toString.endsWith(".sbt"))
      .as(dir)

  def findScalaFolders(root: String) = IO {
    new File(root)
      .listFiles()
      .filter(_.isDirectory)
      .sorted
      .toList
      .flatMap(detectScalaRepo)
  }

  val cmd = "sbt -batch compile"
  val env = "JAVA_HOME" -> "/Library/Java/JavaVirtualMachines/jdk1.8.0_351.jdk/Contents/Home"

  def spawnSbtCompile(dir: File) =
    Process(cmd, dir, env).lazyLines

  val problematicRepos = Set(
  )

  def isProblematic(f: File) = problematicRepos.exists(x => f.toString.endsWith(x))
  def notProblematic(f: File) = !isProblematic(f)

  def compile(f: File) = IO.blocking {
      val repoName = f.toString.split("/").last
      spawnSbtCompile(f).foldLeft(new StringBuilder()) { case (sb, line) =>
          println(s"$repoName: $line")
          sb.append(line)
        }
        .toString()
    }.attempt

  override def run: IO[Unit] =
    fs2.Stream
      .evalSeq(findScalaFolders(root))
      .filter(isProblematic)
//      .filter(notProblematic)
      .evalTap(compile)
//      .parEvalMap(12)(compile)
      .compile
      .drain

}
