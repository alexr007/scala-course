package nqueens

object NQueens extends App {

  def nQueens(n: Int): List[List[Int]] = {

    def isInConflict(newCol: Int, queens: List[Int]): Boolean = {

      /** row: 1..n, col: 1..n */
      def isInConflictOne(newCol: Int, col: Int, row: Int) =
        newCol == col || // same column
          row == (newCol - col) || // same diagonal 1
          row == (col - newCol) // same diagonal 2
      /** we use LazyList.from(1) to handle 0-based index problem */
      (queens zip LazyList.from(1)).exists { case (c, r) => isInConflictOne(newCol, c, r) }
    }

    def nQr(curPos: Int, curQueens: List[Int], solutions: List[List[Int]]): List[List[Int]] =
      if (curPos > n && curQueens.isEmpty) solutions
      else if (curPos > n) /* not empty */ nQr(curQueens.head + 1, curQueens.tail, solutions)
      else if (isInConflict(curPos, curQueens)) nQr(curPos + 1, curQueens, solutions)
      else if (curQueens.length == n - 1) nQr(curPos + 1, curQueens, (curPos :: curQueens) :: solutions)
      else nQr(1, curPos :: curQueens, solutions)

    nQr(1, Nil, Nil) // start from column 1
  }

  val r = nQueens(8)
  println(r.size)
  r.foreach(println)

}
