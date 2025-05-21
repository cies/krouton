package com.natpryce.krouton.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmptyString
import com.natpryce.hamkrest.present
import com.natpryce.krouton.PathTemplate
import com.natpryce.krouton.PathTemplate2
import com.natpryce.krouton.Projection
import com.natpryce.krouton.ROOT
import com.natpryce.krouton.Tuple3
import com.natpryce.krouton.asA
import com.natpryce.krouton.div
import com.natpryce.krouton.getValue
import com.natpryce.krouton.http4k.resources
import com.natpryce.krouton.int
import com.natpryce.krouton.locale
import com.natpryce.krouton.path
import com.natpryce.krouton.string
import com.natpryce.krouton.tuple
import dev.minutest.rootContext
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter
import java.util.*
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.FOUND
import org.http4k.core.Status.Companion.MOVED_PERMANENTLY
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.platform.commons.annotation.Testable


/** An application-specific mapping between parsed URL elements and typed data. */
object Tuple3LocalDate : Projection<Tuple3<Int, Int, Int>, LocalDate> {

  override fun fromParts(parts: Tuple3<Int, Int, Int>): LocalDate? {
    val (year, month, day) = parts
    return try {
      LocalDate.of(year, month, day)
    } catch (_: DateTimeException) {
      null
    }
  }

  override fun toParts(mapped: LocalDate) =
    tuple(mapped.year, mapped.monthValue, mapped.dayOfMonth)
}

// Components of the application's routes
val year by int
val month by int
val day by int
val date = year / month / day asA Tuple3LocalDate

// The application's routes
val reverse = ROOT / "reverse" / string
val negate = ROOT / "negate" / int

// Note: without these explicit type declarations, the Kotlin compiler crashes with an internal error
val weekday: PathTemplate2<Locale, LocalDate> = ROOT / "weekday" / locale.named("locale") / date
val weekdayToday: PathTemplate<Locale> = ROOT / "weekday" / locale.named("locale") / "today"

// Obsolete routes that each redirect to one of the routes above
val negative = ROOT / "negative" / int
val reversed = ROOT / "reversed" / string


// The server that uses the routes
val demo = resources {
  ROOT methods {
    GET { ok("Hello, World.") }
  }

  negate methods {
    GET { _, i -> ok((-i).toString()) }
  }

  negative methods {
    // Note - reverse routing from integer to URL path
    GET { _, i -> redirect(MOVED_PERMANENTLY, negate.path(i)) }
  }

  reverse methods {
    GET { _, s -> ok(s.reversed()) }
  }

  reversed methods {
    GET { _, s -> redirect(MOVED_PERMANENTLY, reverse.path(s)) }
  }

  weekday methods {
    GET { _, (locale, date) -> ok(date.format(DateTimeFormatter.ofPattern("EEEE", locale))) }
  }

  weekdayToday methods {
    /* Note - reverse routing using user-defined projection*/
    GET { _, locale -> redirect(FOUND, weekday.path(locale, now())) }
  }
}

private fun ok(s: String) =
  Response(OK).body(s)

private fun redirect(status: Status, location: String) =
  Response(status).header("Location", location)


@Testable
fun `HttpRouting tests`() = rootContext<HttpHandler> {
  fixture { demo }

  test("negate") {
    assertThat(responseBodyOfGetRequestTo("/negate/100"), equalTo("-100"))
  }

  test("negative_redirects_to_negate") {
    assertThat(responseBodyOfGetRequestTo("/negative/20"), equalTo("-20"))
  }

  test("reverse") {
    assertThat(responseBodyOfGetRequestTo("/reverse/hello%20world"), equalTo("dlrow olleh"))
  }

  test("weekday") {
    assertThat(responseBodyOfGetRequestTo("/weekday/en/2016/02/29"), equalTo("Monday"))
    assertThat(responseBodyOfGetRequestTo("/weekday/fr/2016/02/29"), equalTo("lundi"))
    assertThat(responseBodyOfGetRequestTo("/weekday/de/2016/02/29"), equalTo("Montag"))
    assertThat(responseBodyOfGetRequestTo("/weekday/en/2016/03/01"), equalTo("Tuesday"))
  }

  test("weekday_today") {
    assertThat(responseBodyOfGetRequestTo("/weekday/en/today"), present(!isEmptyString))
  }

  test("bad_dates_not_found") {
    assertThat(responseOfGetRequestTo("/weekday/2016/02/30").status, equalTo(NOT_FOUND))
  }

  test("root") {
    assertThat(responseBodyOfGetRequestTo("/"), equalTo("Hello, World."))
  }
}

