package cli.crawler

import java.io.File

class RecursiveFolderCrawler(callback: (File, Int) => Unit) {

  private def scan(file: File, level: Int): Unit = file match {
    case fd if fd.isDirectory => file.listFiles.foreach(f => scan(f, level + 1))
    case file                 => callback(file, level)
  }

  def run(file: File): Unit = scan(file, 0)

}
