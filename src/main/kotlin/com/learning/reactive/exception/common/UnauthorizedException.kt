package com.learning.reactive.exception.common

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedException(
    message: String
) : RuntimeException(message) {
}