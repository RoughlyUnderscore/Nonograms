package com.roughlyunderscore

import com.roughlyunderscore.admin.configureAdministration
import com.roughlyunderscore.json.configureSerialization
import com.roughlyunderscore.logs.configureMonitoring
import com.roughlyunderscore.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
  EngineMain.main(args)
}

fun Application.module() {
  configureAdministration()
  configureSerialization()
  configureMonitoring()
  configureRouting()
}
