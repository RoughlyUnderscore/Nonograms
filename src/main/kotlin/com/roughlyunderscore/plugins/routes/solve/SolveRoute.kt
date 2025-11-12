package com.roughlyunderscore.plugins.routes.solve

import com.roughlyunderscore.plugins.model.Board
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.isSuccess
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable

fun Route.solveRoute() {
  get("/solve") {
    val response = coroutineScope {
      // Scale parsing
      val scaleString = call.parameters["scale"]
        ?: return@coroutineScope BadRequest to "No scale parameter"

      val scaleParse = ScaleValidator.validate(scaleString, "scale")
      val scale = scaleParse.getOrNull()
        ?: return@coroutineScope BadRequest to scaleParse.leftOrNull()

      // Rows parsing
      val rowString = call.parameters["rows"]
        ?: return@coroutineScope BadRequest to "No rows parameter"

      val rowParse = RowColumnValidator.validate(rowString, "rows")
      val rows = rowParse.getOrNull()
        ?: return@coroutineScope BadRequest to rowParse.leftOrNull()

      // Columns parsing
      val columnString = call.parameters["columns"]
        ?: return@coroutineScope BadRequest to "No columns parameter"

      val columnParse = RowColumnValidator.validate(columnString, "columns")
      val columns = columnParse.getOrNull()
        ?: return@coroutineScope BadRequest to columnParse.leftOrNull()

      val board = Board(
        scale = scale,
        rowClues = rows,
        columnClues = columns
      )

      val crossesString = call.parameters["crosses"]
      if (crossesString != null) {
        val crossesParse = CrossesValidator
          .validate(crossesString, "crosses")
          .getOrNull()

        if (crossesParse != null)
          for ((row, crosses) in crossesParse.entries) board.crossesByList(row) { crosses }
      }

      try {
        return@coroutineScope OK to (board to board.solve())
      } catch (ex: Exception) {
        return@coroutineScope InternalServerError to "Error: ${ex.message}"
      }
    }

    val (result, output) = response
    if (!result.isSuccess()) {
      call.respond(result, output as? String ?: "???")
      return@get
    }

    val (board, solution) = output as Pair<*, *>
    val (iters, ms) = solution as Pair<*, *>

    call.respond(result, object {
      val iterations = iters as Int
      val timeElapsed = ms as Long
      val rows = (board as Board).board.map { it.toList() }
    })
  }
}