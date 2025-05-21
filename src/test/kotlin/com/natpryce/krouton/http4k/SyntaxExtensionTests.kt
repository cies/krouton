package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.ROOT
import com.natpryce.krouton.div
import com.natpryce.krouton.path
import dev.minutest.rootContext
import java.time.LocalTime
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.platform.commons.annotation.Testable

@Testable
fun `extending the Krouton syntax`() = rootContext {
  val internalRoot = ROOT / "internal"
  val status = internalRoot / "status"
  val config = internalRoot / "config"

  fun ResourceRoutesBuilder.canBeMonitored() {
    status methods {
      GET {
        Response(OK).body("it's ${LocalTime.now()} and all's well")
      }
    }

    config methods {
      GET {
        Response(OK).body(System.getenv().entries.joinToString("\n") { (k, v) -> "$k = $v" })
      }
    }
  }

  val myApp = ROOT / "myapp"

  val app = resources {
    myApp methods {
      GET {
        Response(OK).body("not a very useful app!")
      }
    }

    canBeMonitored()
  }

  test("can add common routes") {
    assertThat(app(Request(GET, myApp.path())).status, equalTo(OK))
    assertThat(app(Request(GET, status.path())).status, equalTo(OK))
    assertThat(app(Request(GET, config.path())).status, equalTo(OK))
    assertThat(app(Request(GET, "/not/there")).status, equalTo(NOT_FOUND))
  }
}
