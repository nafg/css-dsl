package cssdsl.bootstrap3

import munit.FunSuite
import zio.http.template2._

class ZioHttpTemplate2DslSpec extends FunSuite {
  test("chain CSS classes onto a ZIO HTTP template2 element") {
    import Dsl._

    val result =
      div(className := C.panelSuccess).panel.hiddenXs(
        h3.panelTitle("Panel title")
      )

    assertEquals(
      result.render,
      """<div class="panel-success panel hidden-xs"><h3 class="panel-title">Panel title</h3></div>"""
    )
  }

  test("compose C values in a template2 className attribute") {
    import Dsl._

    val result = div(className := (C.panel, C.hiddenXs))

    assertEquals(result.render, """<div class="panel hidden-xs"></div>""")
  }

  test("compose C collections in a template2 className attribute") {
    import Dsl._

    val result = div(className := List(C.panel, C.hiddenXs))

    assertEquals(result.render, """<div class="panel hidden-xs"></div>""")
  }

  test("do not convert C values into template2 text children") {
    val errors = compileErrors("""
      import cssdsl.bootstrap3.Dsl._
      import zio.http.template2._
      div(C.panel)
    """)

    assert(errors.nonEmpty)
  }

  test("preserve an existing single-value class attribute") {
    import Dsl._

    val result = div(Dom.attr("class", "custom")).panel

    assertEquals(result.render, """<div class="custom panel"></div>""")
  }

  test("normalize an existing class attribute to space separation") {
    import Dsl._

    val result = div(Dom.multiAttr("class", AttributeSeparator.Comma, "custom")).panel

    assertEquals(result.render, """<div class="custom panel"></div>""")
  }
}
