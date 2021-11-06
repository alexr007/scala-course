package degoes.tips12

/**
  * https://kubuszok.com/2018/adt-through-the-looking-glass/#prism
  */
class Tip07TypeBoundsForVariance {
  // first intention
  trait ProductID
  trait MovieID extends ProductID
  // DO NOT use type bounds, dont constraint !
  trait Product[T <: ProductID] {
    def id: T
  }

  trait Movie extends Product[MovieID]

  // better. DO NOT limit yourself and remove entangling
  trait ProductGood[T] {
    def id: T
    // runtime proof that every T is a ProductID
//    def prism: Prism[ProductID, T]
  }
  trait MovieGood extends ProductGood[MovieID]

}
