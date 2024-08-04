package com.learning.reactive.exception.security

open class ApiException(
    val errorCode: String,
    message: String? = null,
) : RuntimeException(message) {

}