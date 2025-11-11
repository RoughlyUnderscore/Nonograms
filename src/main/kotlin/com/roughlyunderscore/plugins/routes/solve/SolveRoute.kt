package com.roughlyunderscore.plugins.routes.solve

import com.roughlyunderscore.plugins.routes.solve.Scale.validateScale
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.coroutineScope

fun Route.solveRoute() {
  get("/solve") {
    val response = coroutineScope {
      val scaleString = call.parameters["scale"]
        ?: return@coroutineScope HttpStatusCode.BadRequest to "No scale parameter"

      val scale = scaleString.validateScale()
      if (scale.isLeft()) return@coroutineScope HttpStatusCode.BadRequest to (scale.value as Scale.Fail).error
    }
  }
}