package com.roughlyunderscore.plugins.json

import io.ktor.serialization.gson.gson
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
  install(ContentNegotiation) {
    gson()
  }
}
