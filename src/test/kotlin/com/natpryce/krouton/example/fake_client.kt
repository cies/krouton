package com.natpryce.krouton.example

import kotlin.test.fail
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response


internal fun HttpHandler.responseBodyOfGetRequestTo(path: String): String {
  val response = this.responseOfGetRequestTo(path)
  return when {
    response.status.successful -> response.bodyString()
    else -> fail("request failed, status: ${response.status}")
  }
}

internal fun HttpHandler.responseOfGetRequestTo(path: String): Response {
  Request(GET, path)
  val response = this(Request(GET, path))
  return when {
    response.status.redirection -> this@responseOfGetRequestTo.responseOfGetRequestTo(
      response.header("location") ?: fail("no redirect location")
    )

    else -> response
  }
}
