package degoes.tips12

import degoes.tips12.Tip05TypeClasses.NumberLike

class Tip08TypeBoundsOnlyForVariance {
  // dont use type bounding
  def sort1[A <: NumberLike[A]](list: List[A]): List[A] = ???
  // use type classes
  def sort2[A: Numeric](list: List[A]): List[A] = ???
}
