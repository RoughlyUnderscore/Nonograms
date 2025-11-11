package com.roughlyunderscore.plugins.routes.solve

import arrow.core.Either
import java.util.regex.Pattern

object Scale {
  val SCALE_REGEX = Pattern.compile("""(\d+)[x;\-,A-Za-z](\d+)""")!!

  enum class Fail(val error: String) {
    INVALID_SYNTAX("Incorrect syntax for parameter scale")
  }

  fun String.validateScale(): Either<Fail, Pair<Int, Int>> {
    val matcher = SCALE_REGEX.matcher(this)
    if (matcher.groupCount() != 2) return Either.Left(Fail.INVALID_SYNTAX)

    val width = matcher.group(0).toIntOrNull()
    val height = matcher.group(1).toIntOrNull()

    if (width == null || height == null) return Either.Left(Fail.INVALID_SYNTAX)

    return Either.Right(width to height)
  }
}