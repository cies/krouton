package com.natpryce.krouton.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.ROOT
import com.natpryce.krouton.div
import dev.minutest.rootContext
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.platform.commons.annotation.Testable

@Testable
fun `composition of routers`() = rootContext {
  val routeX = ROOT / "x"
  val appX = resources {
    routeX methods {
      GET { Response(OK).body("x") }
    }
  }

  val routeY = ROOT / "y"
  val appY = resources {
    routeY methods {
      GET { Response(OK).body("y") }
    }
  }

  val composedApp = appX + appY

  val monolithicApp = resources {
    routeX methods {
      GET { Response(OK).body("x") }
    }

    routeY methods {
      GET { Response(OK).body("y") }
    }
  }

  test("you can add Krouton apps together") {
    assertThat(composedApp.urlTemplates(), equalTo(monolithicApp.urlTemplates()))
    assertThat(composedApp.router.handlerIfNoMatch, equalTo(appY.router.handlerIfNoMatch))
  }
}
