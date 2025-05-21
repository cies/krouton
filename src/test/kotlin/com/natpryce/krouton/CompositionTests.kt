package com.natpryce.krouton

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.minutest.rootContext
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.platform.commons.annotation.Testable

const val baseUrl = "http://test.com"

@Testable
fun `composition of routes`() = rootContext {
  test("route prefixed single element") {
    assertThat((ROOT / "foo" / string).parse("/foo/bob"), equalTo("bob"))
    assertThat((ROOT / "bar" / int).parse("/bar/99"), equalTo(99))
  }

  test("reverse route for prefixed single element") {
    assertThat((ROOT / "bob" / string).path("xxx"), equalTo("/bob/xxx"))
    assertThat((ROOT / "foo" / int).path(72), equalTo("/foo/72"))
  }

  test("reverse full URL route for prefixed single element") {
    assertThat((ROOT / "bob" / string).fullUrl(baseUrl, "xxx"), equalTo("http://test.com/bob/xxx"))
    assertThat((ROOT / "foo" / int).fullUrl(baseUrl, 72), equalTo("http://test.com/foo/72"))
  }

  test("route prefixed single element when failing") {
    assertThat((ROOT / "a" / int).parse("/a"), absent())
    assertThat((ROOT / "b" / int).parse("/b/not-an-int"), absent())
    assertThat((ROOT / "c" / int).parse("/c/10/unwanted-suffix"), absent())
    assertThat((ROOT / "d" / int).parse("/c/10"), absent())
  }

  test("route suffixed single element") {
    assertThat((string / "foo").parse("/bob/foo"), equalTo("bob"))
    assertThat((int / "bar").parse("/99/bar"), equalTo(99))
  }

  test("reverse route for suffixed single element") {
    assertThat((string / "bob").path("xxx"), equalTo("/xxx/bob"))
    assertThat((int / "foo").path(72), equalTo("/72/foo"))
  }

  test("route suffixed single element when failing") {
    assertThat((int / "a").parse("/10"), absent())
    assertThat((int / "b").parse("/not-an-int/b"), absent())
    assertThat((int / "c").parse("/unwanted-prefix/10/c"), absent())
    assertThat((int / "d").parse("/10/unwanted-suffix"), absent())
  }

  test("combined routes") {
    assertThat((int / string).parse("/9/alice"), equalTo(tuple(9, "alice")))
    assertThat((ROOT / int / string).parse("/9/alice"), equalTo(tuple(9, "alice")))
    assertThat((ROOT / "foo" / string / int).parse("/foo/bob/10"), equalTo(tuple("bob", 10)))
  }

  test("reverse route") {
    assertThat((int / string).path(tuple(1, "ten")), equalTo("/1/ten"))
    assertThat((int / string).path(10, "ten"), equalTo("/10/ten"))
  }

  test("reverse route with full URL") {
    assertThat((int / string).fullUrl(baseUrl, tuple(1, "ten")), equalTo("http://test.com/1/ten"))
    assertThat((int / string).fullUrl(baseUrl, 10, "ten"), equalTo("http://test.com/10/ten"))
  }

  test("reverse route with full URL using Request") {
    val request = Request(Method.GET, "$baseUrl/some/path")
    assertThat((int / string).fullUrl(request, tuple(1, "ten")), equalTo("http://test.com/1/ten"))
    assertThat((int / string).fullUrl(request, 10, "ten"), equalTo("http://test.com/10/ten"))
  }

  // TODO: Should this be like this? I think it's better nog to allow ROOT in any other place than path start.
  test("root acts as a zero path") {
    assertThat((ROOT / string).parse("/foo"), equalTo("foo"))
    assertThat((string / ROOT).parse("/foo"), equalTo("foo"))
    assertThat((ROOT / string / ROOT).parse("/foo"), equalTo("foo"))
  }

  test("stress test") {
    val crazyScheme: PathTemplate<String> = ROOT / "first" / "mid1" / string / "mid2" / "last"
    assertThat(crazyScheme.parse("/first/mid1/foo/mid2/last"), equalTo("foo"))
  }
}
