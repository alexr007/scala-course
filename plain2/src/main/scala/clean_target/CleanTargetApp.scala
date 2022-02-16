package clean_target

import java.io.File

object CleanTargetApp extends App {

  val suffixesToDetect = Set("pom.xml", "build.sbt", "gradlew.bat", "/src", "/project")
  val rootFolder = "/Users/alexr/dev"

  def filter(f: File) = suffixesToDetect.exists { suffix => f.toString.toLowerCase.endsWith(suffix) }

  def validateRootPath(root: String) = {
    val path = new File(root)
    if (!path.exists) sys.error("valid file or folder name is expected as a parameter to run the app")
    path
  }

  val root: File = validateRootPath(rootFolder)
  println(root)

  def callback(file: File): Unit = if (filter(file)) TargetFolder.remove(file) else ()

  new RecursiveFolderCrawler(callback).scan(root)

}
