package com.roughlyunderscore.plugins.routes

import com.roughlyunderscore.plugins.routes.solve.solveRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
  install(StatusPages) {
    exception<Throwable> { call, cause ->
      call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
    }
  }

  routing {
    route("/api/v1") {
      solveRoute()
    }
  }
}