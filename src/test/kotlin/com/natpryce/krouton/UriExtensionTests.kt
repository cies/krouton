package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.minutest.rootContext
import java.net.URI
import org.junit.platform.commons.annotation.Testable

@Testable
fun `extend URI path with template and parameters`() = rootContext {
  val alice = ROOT / "alice"

  test("no parameters") {
    assertThat(URI("http://example.com").extend(alice), equalTo(URI("http://example.com/alice")))
    assertThat(URI("http://example.com/").extend(alice), equalTo(URI("http://example.com/alice")))

    assertThat(URI("http://example.com/bob").extend(alice), equalTo(URI("http://example.com/bob/alice")))
    assertThat(URI("http://example.com/bob/").extend(alice), equalTo(URI("http://example.com/bob/alice")))
  }

  val aliceX = alice / string.named("x")

  test("parameterised") {
    assertThat(
      URI("http://example.com/dave").extend(aliceX, "carol"),
      equalTo(URI("http://example.com/dave/alice/carol"))
    )
    assertThat(
      URI("http://example.com/dave/").extend(aliceX, "carol"),
      equalTo(URI("http://example.com/dave/alice/carol"))
    )
  }

  test("escaping") {
    assertThat(
      URI("http://example.com/").extend(aliceX, "foo/bar"),
      equalTo(URI("http://example.com/alice/foo%2Fbar"))
    )
  }
}
