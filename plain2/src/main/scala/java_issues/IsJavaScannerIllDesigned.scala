package java_issues

import java.util.Scanner

// update: it closes underline stream,
// and as a result, closes System.in
object IsJavaScannerIllDesigned extends App {

  val in = System.in

  val s1 = new Scanner(in)
  val line1 = s1.next()
  s1.close()

  val s2 = new Scanner(in)
  val line2 = s2.next() // java.util.NoSuchElementException
  s2.close()

}
