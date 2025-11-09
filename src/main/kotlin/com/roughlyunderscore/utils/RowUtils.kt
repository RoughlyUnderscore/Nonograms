package com.roughlyunderscore.utils

import com.github.michaelbull.itertools.product
import com.roughlyunderscore.model.Cell
import com.roughlyunderscore.model.CellState
import kotlin.math.max

typealias Row = Array<Cell>

/**
 * Fills the cells in this row from and to the given positions (assumes that
 * the positions are 1-indexed, but can be toggled) with the provided cell state.
 */
fun Row.fillFromTo(from: Int, to: Int, with: CellState, indexationStart: Int = 1) =
  replaceFromTo(from, to, with, null, indexationStart)

/**
 * Replaces the cells in this row from and to the given positions (assumes that
 * the positions are 1-indexed, but can be toggled) with the provided cell state.
 */
fun Row.replaceFromTo(from: Int, to: Int, with: CellState, replace: CellState?, indexationStart: Int = 1) {
  for (idx in from..to) {
    val cell = this[max(0, idx - indexationStart)]
    if (cell.state == replace || replace == null) cell.state = with
  }
}

/**
 * If the row is filled, adds crosses wherever they are missing.
 *
 * Visual example:
 * - 2, (----OO----) -> (xxxxOOxxxx)
 */
fun Row.crossOutFilled(clues: List<Int>): Boolean {
  if (count { it.state == CellState.FILLED } == clues.sum()) {
    replaceFromTo(1, size, CellState.CROSSED, CellState.EMPTY)
    return true
  }

  return false
}

/**
 * Use the basic deterministic method for iteratively
 * solving a nonogram (check all possible layouts per row
 * and mark any cells that share a common state across
 * all the layouts).
 *
 * Example:
 * - 1, 4, 2, (----------) -> (---OOO--O-)
 */
fun Row.intersectionMethod(clues: List<Int>): Boolean {
  if (none { it.state == CellState.EMPTY }) return false

  var changesDetected = false
  val layouts = mutableListOf<List<CellState>>()

  val cluesTotal = clues.sum()

  // Iterate through all possible cross amounts
  // E.g. for clues [6,1] there can be three cross chains (before, between, after)
  // and the total complementary amount is 10 - (6+1) = 3
  // Thus iterate from (0, 0, 0) to (3, 3, 3) and discard any invalid combinations
  val crossAmounts = clues.size + 1

  val range = (0..(size - cluesTotal)).toList()
  val exclusiveRange = (1..(size - cluesTotal)).toList()
  val iterations = (
    if (crossAmounts == 2) listOf(range.toList(), range.toList())
    else listOf(range) + List(crossAmounts - 2) { exclusiveRange } + listOf(range)
  ).product().toList()

  for (product in iterations) {
    // Immediately discard products that don't satisfy the size requirement
    if (product.sum() + cluesTotal != size) continue

    val layout = Row(size) { Cell() }

    // EXAMPLE | Clues: [6, 1]; crosses: [1, 1, 1]
    // Go through [0; crossAmounts * 2)
    // If even, fill the crosses; if odd, fill the clues.
    var pos = 1
    for (i in (0 ..< crossAmounts * 2 - 1)) {
      val idx = i / 2
      val length: Int
      if (i % 2 == 0) {
        length = product[idx]
        layout.fillFromTo(pos, pos + length - 1, CellState.CROSSED)
      } else {
        length = clues[idx]
        layout.fillFromTo(pos, pos + length - 1, CellState.FILLED)
      }

      pos += length
    }

    // Check whether the layout overlaps with any of the previously established data
    var badLayout = false
    for (idx in layout.indices) {
      val member = layout[idx].state
      val corresponding = this[idx].state
      if ((member == CellState.CROSSED && corresponding == CellState.FILLED) ||
        (member == CellState.FILLED && corresponding == CellState.CROSSED)) badLayout = true
    }

    if (badLayout) continue

    // Add this layout to the total list of layouts
    layouts.add(layout.map { it.state })
  }

  // Check whether there are any cells that share common cell states among all
  // existing layouts
  for (idx in 0 ..< size) {
    val members = layouts.map { it[idx] }
    if (members.toSet().size == 1) {
      this[idx].state = members[0]
      changesDetected = true
    }
  }

  return changesDetected
}