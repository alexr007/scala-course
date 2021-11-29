package ninetynine

import tools.spec.ASpec

/** Determine whether a given integer number is prime
  *
  * http://primes.utm.edu/prove/index.html http://article.gmane.org/gmane.comp.lang.haskell.cafe/19470
  */
object P31 {

  // LAZY PRIMES GENERATION
  def isPrime(x: Int): Boolean = (x > 1) && (primes takeWhile { _ <= math.sqrt(x) } forall { x % _ != 0 })
  val primes: LazyList[Int] = LazyList.cons(2, LazyList.from(3, 2) filter isPrime)

  def isPrimeNaive(a: Int): Boolean =
    (a > 1) && LazyList.from(2).take(math.sqrt(a).toInt + 1).filter(_ < a).forall(a % _ != 0)
}

class P31Spec extends ASpec {
  import P31._

  it("100") {
    println(primes.take(100))
  }

  it("0") {
    println(isPrime(1000000007))
  }

  it("1") {
    val t = Seq(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31).map(_ -> true)
    val f = Seq(1, 4, 6, 8, 9, 10, 12, 14, 15, 16, 18, 20).map(_ -> false)

    runAllD(t ++ f, isPrime _)
  }

}
