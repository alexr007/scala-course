package cookbook.x008

object TraitsApp extends App {
  new Object with BaseSoundPlayer {
    override def play: Unit = ???
    override def stop = "stopping"
    override def pause = println("pausing")
    override def resume: String = { "resumed" }

    // for previously undefined fields
    override var amount: Int = 7
    // for constants
    override val SIZE = 31
  }

}
