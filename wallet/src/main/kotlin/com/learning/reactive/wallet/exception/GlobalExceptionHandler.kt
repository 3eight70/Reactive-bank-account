package com.learning.reactive.wallet.exception

import com.learning.reactive.common.dto.response.CustomFieldError
import com.learning.reactive.common.dto.response.Response
import com.learning.reactive.wallet.exception.common.*
import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(e: WebExchangeBindException): ResponseEntity<Response> {
        val fieldErrors: List<CustomFieldError> = e.bindingResult.fieldErrors.stream()
            .map { error -> CustomFieldError(error.field, error.defaultMessage) }
            .toList()

        val response = Response(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Валидация провалена",
            errors = fieldErrors
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handle(e: ForbiddenException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.FORBIDDEN.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(e: NotFoundException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.NOT_FOUND.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(e: BadRequestException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.BAD_REQUEST.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(e: ServerWebInputException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.BAD_REQUEST.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handle(e: UnauthorizedException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handle(e: ExpiredJwtException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = "Время жизни токена истекло"
        )

        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(CustomTimeoutException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun handle(e: CustomTimeoutException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.SERVICE_UNAVAILABLE)
    }

    @ExceptionHandler(RedisConnectionFailureException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun handle(e: RedisConnectionFailureException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
            message = "В данный момент сервис недоступен, попробуйте позже..."
        )

        return ResponseEntity(response, HttpStatus.SERVICE_UNAVAILABLE)
    }


    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handle(e: Exception): ResponseEntity<Response> {
        log.error(e.printStackTrace().toString())

        val response = Response(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "Что-то пошло не так"
        )

        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}