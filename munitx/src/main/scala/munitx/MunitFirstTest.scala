package munitx

import munit._

// https://www.youtube.com/watch?v=4ZQNxcdeK_g
class MunitFirstTest extends FunSuite {

  // transformation!

  test("1+1=2") {
    assertEquals(
      1 + 1,
      2
    )
  }

}
