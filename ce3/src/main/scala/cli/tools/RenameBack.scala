package cli.tools

import java.io.File

object RenameBack extends App {
  val root = "/Users/alexr/dev/sym/_non-scala"

  def renameBack(dir: File) = {
    val chunks = dir.toString.split("/")
    val newName = chunks.init :+ chunks.last.stripPrefix("0.")
    val newPath = newName.mkString("/", "/", "")
    dir.renameTo(new File(newPath))
  }

  new File(root)
    .listFiles()
    .filter(_.isDirectory)
    .foreach(renameBack)

}
