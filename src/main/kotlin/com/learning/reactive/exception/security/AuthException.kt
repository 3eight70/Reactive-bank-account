package com.learning.reactive.exception.security

class AuthException(
    message: String,
    errorCode: String,
) : ApiException(errorCode, message) {
}