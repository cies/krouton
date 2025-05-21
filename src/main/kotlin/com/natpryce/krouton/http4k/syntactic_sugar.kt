package com.natpryce.krouton.http4k

import com.natpryce.krouton.PathTemplate
import kotlin.DeprecationLevel.ERROR
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status


@DslMarker
annotation class RoutingSyntax

@RoutingSyntax
class ResourceRoutesBuilder {
  private val routes = mutableListOf<PathParsingRoute<*>>()
  private var handlerIfNoMatch: HttpHandler = { Response(Status.NOT_FOUND) }

  operator fun <T> PathTemplate<T>.invoke(handler: (Request, T) -> Response) {
    addPathHandler(this, handler)
  }

  operator fun PathTemplate<Unit>.invoke(handler: (Request) -> Response) {
    addPathHandler(this, emptyHandler(handler))
  }

  infix fun <T> PathTemplate<T>.methods(block: MethodRoutesBuilder<T>.() -> Unit) {
    addPathHandler(this, MethodRoutesBuilder<T>().apply(block).toHandler())
  }

  @JvmName("methods0")
  infix fun PathTemplate<Unit>.methods(block: MethodRoutesBuilderUnit.() -> Unit) {
    addPathHandler(this, MethodRoutesBuilderUnit().apply(block).toHandler())
  }

  fun otherwise(handler: HttpHandler) {
    handlerIfNoMatch = handler
  }

  private fun <T> addPathHandler(pathTemplate: PathTemplate<T>, handler: Request.(T) -> Response) {
    routes.add(PathParsingRoute(pathTemplate, handler))
  }

  @Suppress("DeprecatedCallableAddReplaceWith")
  @Deprecated(level = ERROR, message = "Don't nest resources blocks")
  fun resources(setup: ResourceRoutesBuilder.() -> Unit): Nothing =
    throw UnsupportedOperationException("don't nest resources blocks")

  fun toHandler() = ResourceRouter(routes.toList(), handlerIfNoMatch)
}

@RoutingSyntax
class MethodRoutesBuilder<T> {
  private val routes = mutableListOf<(Request, T) -> Response?>()
  private var handlerIfNoMatch: (Request, T) -> Response = { _, _ -> Response(Status.METHOD_NOT_ALLOWED) }

  operator fun Method.invoke(handler: (Request, T) -> Response) {
    routes += methodHandler(this, handler)
  }

  fun otherwise(handler: (Request, T) -> Response) {
    handlerIfNoMatch = handler
  }

  fun toHandler() = Router(routes, handlerIfNoMatch)
}

@RoutingSyntax
class MethodRoutesBuilderUnit {
  private val routes = mutableListOf<(Request, Unit) -> Response?>()
  private var handlerIfNoMatch: (Request) -> Response = { Response(Status.METHOD_NOT_ALLOWED) }

  operator fun Method.invoke(handler: (Request) -> Response) {
    val requiredMethod = this
    routes += methodHandler(requiredMethod, emptyHandler(handler))
  }

  fun otherwise(handler: (Request) -> Response) {
    handlerIfNoMatch = handler
  }

  fun toHandler() = Router(routes, emptyHandler(handlerIfNoMatch))
}

private fun emptyHandler(handler: (Request) -> Response) = { r: Request, _: Unit -> handler(r) }


fun resources(setup: ResourceRoutesBuilder.() -> Unit) = ResourceRoutesBuilder().apply(setup).toHandler()
