package cli.crawler

import java.io.File
import scala.sys.process.Process

object FolderOps {

  def dirContents(file: File): List[File] =
    Option(file.listFiles)
      .toList
      .flatten

  def removeRecursively(file: File): Unit = {
    dirContents(file)
      .foreach(removeRecursively)

    file.delete
  }

  def removeTarget(file: File): Unit = {
    val chunks = file.toPath.toString.split("/")
    val target = chunks.init :+ "target"
    val toDeleteF = new File(target.mkString("/", "/", ""))

    if (toDeleteF.exists && toDeleteF.isDirectory) {
      println(s"detected: $file, removing: $toDeleteF")
      removeRecursively(toDeleteF)
    }
  }

  def rmRF(file: File): Unit = Process(s"rm -rf ${file.toString}").!!

}
