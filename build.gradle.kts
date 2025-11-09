plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ktor)
  alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.roughlyunderscore"
version = "0.1-alpha"

application {
  mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
  implementation(libs.ktor.server.rate.limiting)
  implementation(libs.ktor.server.html.builder)
  implementation(libs.kotlinx.html)
  implementation(libs.ktor.server.core)
  implementation(libs.kotlin.css)
  implementation(libs.ktor.server.content.negotiation)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.ktor.server.call.logging)
  implementation(libs.ktor.server.metrics)
  implementation(libs.ktor.server.host.common)
  implementation(libs.ktor.server.status.pages)
  implementation(libs.ktor.server.netty)
  implementation(libs.logback.classic)
  testImplementation(libs.ktor.server.test.host)
  testImplementation(libs.kotlin.test.junit)
}
