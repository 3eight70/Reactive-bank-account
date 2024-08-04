package com.learning.reactive.exception.security

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedException(
    message: String
    ) : ApiException("UNAUTHORIZED", message) {
}