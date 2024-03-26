package cli.crawler

import java.io.File

trait TraverseFolders {

  def root: String

  val tails: Set[String]

  def action(file: File, level: Int): Unit

  private def validateRootExists(root: String) = new File(root) match {
    case f if !f.exists => sys.error("valid file or folder name is expected as a parameter to run the app")
    case f              => f
  }

  private def filterFn(f: File): Boolean =
    tails.exists(chunk => f.toString.toLowerCase.endsWith(chunk))

  private def doAction(file: File, level: Int): Unit =
    if (filterFn(file)) action(file, level)

  def main(args: Array[String]): Unit = {
    val rootF: File = validateRootExists(root)
    println(s"Root folder: $rootF")

    new RecursiveFolderCrawler(doAction).run(rootF)
  }

}
