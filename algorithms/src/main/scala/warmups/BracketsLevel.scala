package warmups

object BracketsLevel extends App {

  /** we are going to store intermediate calculation results in the tuple (Int, Int)
    *   - t._1 - current nesting level
    *   - t._2 - max nesting level
    */
  def level(origin: String): Int =
    origin
      .foldLeft(0 -> 0) { case ((level, maxLevel), br) =>
        val cur = level + (br match {
          case '(' => +1
          case ')' => -1
        })
        (cur, maxLevel max cur)
      }
      ._2

  Map(
    "()()()"                 -> 1,
    "()"                     -> 1,
    "()(()())()"             -> 2,
    "()(()())((()()))(())()" -> 3,
  ).foreach(t => assert(t._2 == level(t._1)))
}
