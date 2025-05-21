package com.natpryce.krouton

import org.http4k.core.Request


fun PathTemplate0.path() =
  path(Unit)

fun <A, B> PathTemplate2<A, B>.path(a: A, b: B) =
  path(tuple(a, b))

fun <A, B, C> PathTemplate3<A, B, C>.path(a: A, b: B, c: C) =
  path(tuple(a, b, c))

fun <A, B, C, D> PathTemplate4<A, B, C, D>.path(a: A, b: B, c: C, d: D) =
  path(tuple(a, b, c, d))

fun <A, B, C, D, E> PathTemplate5<A, B, C, D, E>.path(a: A, b: B, c: C, d: D, e: E) =
  path(tuple(a, b, c, d, e))


fun PathTemplate0.fullUrl(request: Request) =
  fullUrl(request, Unit)

fun <A, B> PathTemplate2<A, B>.fullUrl(request: Request, a: A, b: B) =
  fullUrl(request, tuple(a, b))

fun <A, B, C> PathTemplate3<A, B, C>.fullUrl(request: Request, a: A, b: B, c: C) =
  fullUrl(request, tuple(a, b, c))

fun <A, B, C, D> PathTemplate4<A, B, C, D>.fullUrl(request: Request, a: A, b: B, c: C, d: D) =
  fullUrl(request, tuple(a, b, c, d))

fun <A, B, C, D, E> PathTemplate5<A, B, C, D, E>.fullUrl(request: Request, a: A, b: B, c: C, d: D, e: E) =
  fullUrl(request, tuple(a, b, c, d, e))


fun PathTemplate0.fullUrl(baseUrl: String) =
  fullUrl(baseUrl, Unit)

fun <A, B> PathTemplate2<A, B>.fullUrl(baseUrl: String, a: A, b: B) =
  fullUrl(baseUrl, tuple(a, b))

fun <A, B, C> PathTemplate3<A, B, C>.fullUrl(baseUrl: String, a: A, b: B, c: C) =
  fullUrl(baseUrl, tuple(a, b, c))

fun <A, B, C, D> PathTemplate4<A, B, C, D>.fullUrl(baseUrl: String, a: A, b: B, c: C, d: D) =
  fullUrl(baseUrl, tuple(a, b, c, d))

fun <A, B, C, D, E> PathTemplate5<A, B, C, D, E>.fullUrl(baseUrl: String, a: A, b: B, c: C, d: D, e: E) =
  fullUrl(baseUrl, tuple(a, b, c, d, e))


fun PathTemplate0.monitoredPath() =
  monitoredPath(Unit)

fun <A, B> PathTemplate2<A, B>.monitoredPath(a: A, b: B) =
  monitoredPath(tuple(a, b))

fun <A, B, C> PathTemplate3<A, B, C>.monitoredPath(a: A, b: B, c: C) =
  monitoredPath(tuple(a, b, c))

fun <A, B, C, D> PathTemplate4<A, B, C, D>.monitoredPath(a: A, b: B, c: C, d: D) =
  monitoredPath(tuple(a, b, c, d))

fun <A, B, C, D, E> PathTemplate5<A, B, C, D, E>.monitoredPath(a: A, b: B, c: C, d: D, e: E) =
  monitoredPath(tuple(a, b, c, d, e))

