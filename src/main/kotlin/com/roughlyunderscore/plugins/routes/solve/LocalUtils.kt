package com.roughlyunderscore.plugins.routes.solve

import arrow.core.Either
import com.roughlyunderscore.plugins.routes.solve.validation.SolveRouteValidator

object ScaleValidator : SolveRouteValidator<Pair<Int, Int>> {
  override fun getValidationRegex() =
    Regex("""(\d+)[x;\-,A-Za-z](\d+)""")

  override fun getSplitRegex() =
    Regex("""[x;\-A-Za-z]""")

  override fun validate(value: String, parameter: String):
    Either<String, Pair<Int, Int>>
  {
    if (!value.matches(getValidationRegex()))
      return Either.Left("Incorrect syntax for parameter $value")

    return Either.Right(value.split(getSplitRegex(), limit = 2).let { parts ->
      val width = parts[0].toInt()
      val height = parts[1].toInt()
      width to height
    })
  }
}

object RowColumnValidator : SolveRouteValidator<List<List<Int>>> {
  override fun getValidationRegex() =
    Regex("""^\d+(?: \d+)*(?:[;,\-]\d+(?: \d+)*)*$""")

  override fun getSplitRegex() =
    Regex("""[;,\-]""")

  override fun validate(value: String, parameter: String):
    Either<String, List<List<Int>>>
  {
    if (!value.matches(getValidationRegex()))
      return Either.Left("Incorrect syntax for parameter $parameter")

    return Either.Right(value.split(getSplitRegex()).map { list ->
      list.split(" ").map { element -> element.toInt() }
    })
  }
}

object CrossesValidator : SolveRouteValidator<Map<Int, List<Int>>> {
  override fun getValidationRegex() =
    Regex("""^\d+,\d+(?: \d+)*(?:;\d+,\d+(?: \d+)*)*$""")

  override fun getSplitRegex() = null

  override fun validate(value: String, parameter: String):
    Either<String, Map<Int, List<Int>>>
  {
    if (!value.matches(getValidationRegex()))
      return Either.Left("Incorrect syntax for parameter $value")

    return Either.Right(value.split(";").associate { part ->
      val (rowString, crossesString) = part.split(",", limit = 2)
      val row = rowString.toInt()
      val crosses = crossesString.split(" ").map { it.toInt() }
      row to crosses
    })
  }
}