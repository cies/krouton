# Krouton

Typesafe, compositional routing and reverse routing for [http4k](https://http4k.org) web apps and web APIs.

[![Kotlin](https://img.shields.io/badge/kotlin-1.2.51-blue.svg)](http://kotlinlang.org)
[![Build Status](https://travis-ci.org/npryce/krouton.svg?branch=master)](https://travis-ci.org/npryce/krouton)
[![Maven Central](https://img.shields.io/maven-central/v/com.natpryce/krouton.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.natpryce%22%20AND%20a%3A%22krouton%22)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

While Krouton integrates out-of-the-box with [http4k](https://http4k.org), its abstractions can be used with any web server library.


## Examples

Here a small overview of how to use Krouton in an `http4k` application.
The `.path(...)` and `.fullUrl(...)` methods show how to do reverse routing, useful for building links in a type-safe manner.

In the `oldApp` you see http4k's standard way of defining routes (with no means of reversing a route a.k.a. link building from routes).

```kotlin
val pingPath = ROOT / "ping"
val multiParamPath = ROOT / "multi" / int / "with-string" / string

val app: HttpHandler = resources {
    pingPath methods {
        GET { _ -> Response(OK).body("pong") }
    }
    multiParamPath methods {
        GET { req, (i, s) ->
            val path = multiParamPath.path(i, s)
            val fullUrl = multiParamPath.fullUrl(req, i, s)
            Response(OK).body("Path: $path (or full URL: $fullUrl)")
        }
    }
}

val oldApp: HttpHandler = routes(
    "/ping" bind GET to { Response(OK).body("pong") }
)

fun main(args: Array<String>) {
    (if (args.contains("OLD")) oldApp else app).asServer(SunHttp(9000)).start()
}

```

More examples can be in the test, like:

* [Routing and reverse routing](src/test/kotlin/com/natpryce/krouton/example/HttpRoutingExample.kt)
* [The calculator example](src/test/kotlin/com/natpryce/krouton/example/CalculatorExample.kt)


## Principles

Type safe routing and reverse routing.

No reflection, annotations or classpath scanning.

Explicit, type-checked flow of data and control, instead of "spooky action at a distance" via reflection, annotations,
classpath scanning, passing data in context maps or synthetic HTTP headers, or control flow via exceptions.

Separate code that routes and handles requests from definitions of URLs

* URLs defined by constants (immutable objects)
* Routing policy defined by operations on those constants
* Routing done by functions/closures/objects that connect Krouton's routing policy API to a web server library.

Compositional: routes are composed from primitive parts and composition operators. User-defined routes can be used in
exactly the same way as the predefined primitives.

Mandatory aspects of a resource locator go in the path

Query parameters are optional and are interpreted by the resource.


## Routing policy operations

* Parsing with `.parse(...)`: `UrlScheme<T>.parse(String) -> T?`
* Reporting with `.monitoredPath(...)`: `UrlScheme<T>.monitoredPath(T)-> String`
* Reverse routing with...
   * `.path(...)`: `UrlScheme<T>.path(T) -> String`
   * `.fullUrl(...)`: `UrlScheme<T>.path(baseUrl: String, T) -> String`
   * `.fullUrl(...)`: `UrlScheme<T>.path(request: Request, T) -> String`


## Route composition

* Append:
    * `UrlScheme<T> / UrlScheme<U> -> UrlScheme<Tuple2<T,U>>`
    * `/` operator supports appending paths for scalars and tuples, forming paths for tuples with up to five elements
    * `UrlScheme<Unit> / UrlScheme<T> -> UrlScheme<T>`
    * `UrlScheme<T> / UrlScheme<Unit> -> UrlScheme<T>`
* Append fixed path element: `UrlScheme<T> / String -> UrlScheme<T>`
* Restrict: `UrlScheme<T> where ((T)->Boolean) -> UrlScheme<T>`
* Project: `UrlScheme<T> asA Projection<T,U> -> UrlScheme<U>`


## What's with the version number?

The version number is {mental}.{major}.{minor}.{patch}. The last three digits are treated as a
[semantic version number](https://semver.org). The first digit is incremented if there is a significant
change in the mental model underpinning the library. A major version of zero always signifies a pre-release version,
irrespective of the value of the first digit. The API of pre-release versions may go through significant changes in
response to user feedback before the release of version x.1.0.0.
