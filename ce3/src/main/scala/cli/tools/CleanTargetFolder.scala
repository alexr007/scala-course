package cli.tools

import cli.crawler.{FolderOps, TraverseFolders}
import java.io.File

object CleanTargetFolder extends TraverseFolders {
  val ~ = "/Users/alexr"
  override val root = s"${~}/dev"
  override val tails: Set[String] = Set("pom.xml", "build.sbt", "gradlew.bat", "/src", "/project")
  override def action(file: File, level: Int): Unit = FolderOps.removeTarget(file)
}
