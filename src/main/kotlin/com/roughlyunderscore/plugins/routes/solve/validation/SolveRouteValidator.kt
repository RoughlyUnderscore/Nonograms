package com.roughlyunderscore.plugins.routes.solve.validation

import arrow.core.Either

interface SolveRouteValidator<K> {
  fun getValidationRegex(): Regex

  fun getSplitRegex(): Regex?

  fun validate(value: String, parameter: String): Either<String, K>
}