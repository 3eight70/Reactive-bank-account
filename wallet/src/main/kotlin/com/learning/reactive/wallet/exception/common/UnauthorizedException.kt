package com.learning.reactive.wallet.exception.common

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedException(
    message: String
) : RuntimeException(message) {
}