package com.roughlyunderscore

import com.roughlyunderscore.plugins.admin.configureRateLimit
import com.roughlyunderscore.plugins.json.configureSerialization
import com.roughlyunderscore.plugins.logs.configureMonitoring
import com.roughlyunderscore.plugins.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
  EngineMain.main(args)
}

fun Application.module() {
  configureRateLimit()
  configureSerialization()
  configureMonitoring()
  configureRouting()
}
