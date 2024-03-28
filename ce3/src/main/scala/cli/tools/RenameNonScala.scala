package cli.tools

import java.io.File

object RenameNonScala extends App {
  val root = "/Users/alexr/dev/sym"

  def detectScala(dir: File): Boolean = dir.listFiles().exists(_.toString.endsWith(".sbt"))

  def rename(dir: File) = {
    val chunks = dir.toString.split("/")
    val newName = chunks.init :+ ("0." + chunks.last)
    val newPath = newName.mkString("/", "/", "")
    dir.renameTo(new File(newPath))
  }

  new File(root)
    .listFiles()
    .filter(_.isDirectory)
    .map(f => detectScala(f) -> f)
    .filterNot { case (isScala, _) => isScala }
    .map { case (_, f) => f }
    .foreach(rename)

}
