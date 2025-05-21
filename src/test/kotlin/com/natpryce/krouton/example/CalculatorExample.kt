package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.PathTemplate2
import com.natpryce.krouton.ROOT
import com.natpryce.krouton.div
import com.natpryce.krouton.double
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.path
import dev.minutest.rootContext
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.MOVED_PERMANENTLY
import org.http4k.core.Status.Companion.OK
import org.junit.platform.commons.annotation.Testable


val operands: PathTemplate2<Double, Double> = double / double

val add: PathTemplate2<Double, Double> = ROOT / "add" / operands
val sub: PathTemplate2<Double, Double> = ROOT / "sub" / operands
val mul: PathTemplate2<Double, Double> = ROOT / "mul" / operands
val div: PathTemplate2<Double, Double> = ROOT / "div" / operands
val max: PathTemplate2<Double, Double> = ROOT / "max" / operands
val min: PathTemplate2<Double, Double> = ROOT / "min" / operands
val avg: PathTemplate2<Double, Double> = ROOT / "avg" / operands

val calculatorService = resources {
  add methods {
    GET { _, (x, y) -> ok(x + y) }
  }
  sub methods {
    GET { _, (x, y) -> ok(x - y) }
  }
  mul methods {
    GET { _, (x, y) -> ok(x * y) }
  }
  div methods {
    GET { _, (x, y) -> ok(x / y) }
  }
  max methods {
    GET { _, (x, y) -> ok(maxOf(x, y)) }
  }
  min methods {
    GET { _, (x, y) -> ok(minOf(x, y)) }
  }
  avg methods {
    GET { _, (x, y) -> ok((x + y) / 2.0) }
  }
  // reverse routing: turn a path template into a link
  ROOT / "average" / operands methods {
    GET { _, (x, y) -> Response(MOVED_PERMANENTLY).header("Location", avg.path(x, y)) }
  }
}

private fun ok(value: Double) = Response(OK).body(value.toString())

@Testable
fun `CalculatorService tests`() = rootContext<HttpHandler> {
  fixture { calculatorService }

  test("adding") {
    assertThat(responseBodyOfGetRequestTo("/add/1/2"), equalTo("3.0"))
  }

  test("subtracting") {
    assertThat(responseBodyOfGetRequestTo("/sub/2.25/0.5"), equalTo("1.75"))
  }
}

