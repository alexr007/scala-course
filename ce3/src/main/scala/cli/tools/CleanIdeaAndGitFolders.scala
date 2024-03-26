package cli.tools

import cli.crawler.{FolderOps, TraverseFolders}
import java.io.File

object CleanIdeaAndGitFolders extends TraverseFolders {
  val ~ = "/Users/alexr"
  override val root = s"${~}/dev/abb/steps"
  override val tails: Set[String] = Set(".git", ".idea", ".iml", ".gitignore")
  override def action(file: File, level: Int): Unit = FolderOps.rmRF(file)
}
