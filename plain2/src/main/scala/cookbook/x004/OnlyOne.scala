package cookbook.x004

class OnlyOne private {}

object OnlyOne {
  def instance = new OnlyOne
}
