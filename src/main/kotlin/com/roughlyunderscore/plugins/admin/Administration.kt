package com.roughlyunderscore.plugins.admin

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit() {
  routing {
    install(RateLimit) {
      global {
        rateLimiter(limit = 20, refillPeriod = 60.seconds)
      }
    }

    get("/") {
      val requestsLeft = call.response.headers["X-RateLimit-Remaining"]
      call.respondText("$requestsLeft left")
    }
  }
}
