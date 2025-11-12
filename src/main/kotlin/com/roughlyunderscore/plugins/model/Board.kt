package com.roughlyunderscore.plugins.model

import com.roughlyunderscore.utils.crossOutFilled
import com.roughlyunderscore.utils.intersectionMethod
import com.roughlyunderscore.utils.swapRowsAndColumns

class Board(
  /**
   * X*Y scale of the board (rows * columns)
   */
  val scale: Pair<Int, Int>,

  /**
   * The clues for each row, starting with the topmost one.
   *
   * Example:
   * - [[2, 7], [6], [7], [1, 1]]
   */
  val rowClues: List<List<Int>>,

  /**
   * The clues for each column, starting with the leftmost one.
   *
   * Example: see [rowClues]
   */
  val columnClues: List<List<Int>>,
) {
  val rows = scale.first
  val cols = scale.second

  /**
   * This property indicates whether there are any empty cells
   * on the board (not crossed or filled).
   */
  val isSolved
    get() = board.all { row -> row.none { it.state == CellState.EMPTY } }

  /**
   * The cells array stores an array of rows.
   */
  val board = Array(rows) {
    Array(cols) { Cell() }
  }

  /**
   * Fills a row with crosses at specific positions.
   * The positions and the row are indexed starting with 1.
   * Returns itself for a builder-esque pattern.
   *
   * Syntax example:
   * - "3:5,7,9" (fills cells 3, 4, 5, 7, 9 in given row)
   */
  fun crosses(row: Int, value: () -> String): Board {
    value().split(",").forEach { part ->
      val range = if (":" in part) part.split(":").let { bounds ->
        (bounds[0].toInt()..bounds[1].toInt()).toList()
      } else listOf(part.toInt())

      for (cell in range)
        board[row - 1][cell - 1].state = CellState.CROSSED
    }

    return this
  }

  /**
   * Fills a row with crosses at specific positions.
   * The positions and the row are indexed starting with 1.
   * Returns itself for a builder-esque pattern.
   */
  fun crossesByList(row: Int, value: () -> List<Int>): Board {
    val crosses = value()
    for (idx in 0..rows) {
      if (idx + 1 in crosses) board[row - 1][idx].state = CellState.CROSSED
    }

    return this
  }

  /**
   * Iteratively goes through the board, filling in as many cells
   * as possible in this given iteration, until the board is solved
   * or no more cells can be filled deterministically.
   *
   * Returns a pair of numbers (iteration count to milliseconds elapsed).
   */
  fun solve(): Pair<Int, Long> {
    var iterations = 0
    val start = System.currentTimeMillis()

    while (!isSolved) {
      iterations++

      var changesDetected = trySolvingRows(board)

      // Swap rows and columns and try again
      var transposedBoard = board.swapRowsAndColumns()
      changesDetected = changesDetected or trySolvingRows(transposedBoard, true)

      // Swap rows and columns back and refill the board
      transposedBoard = transposedBoard.swapRowsAndColumns()
      for (row in 1..rows) {
        for (col in 1..cols) {
          board[row - 1][col - 1] = transposedBoard[row - 1][col - 1]
        }
      }

      // If no changes have been performed at all during this iteration,
      // throw an exception and exit.
      if (!changesDetected) {
        throw IllegalStateException("The board could not be filled any further")
      }
    }

    return iterations to (System.currentTimeMillis() - start)
  }

  private fun trySolvingRows(targetBoard: Array<Array<Cell>>, swapped: Boolean = false): Boolean {
    var changesDetected = false
    for (idx in 1..(if (swapped) cols else rows)) {
      val row = targetBoard[idx - 1]
      val clues = (if (swapped) columnClues else rowClues)[idx - 1]

      changesDetected = changesDetected or
        row.crossOutFilled(clues) or // Pre-processing before intersection checking
        row.intersectionMethod(clues)
    }

    return changesDetected
  }
}