package com.learning.reactive.atm.exception

import com.learning.reactive.atm.exception.deposit.BadAmountOfMoneyException
import com.learning.reactive.common.dto.response.CustomFieldError
import com.learning.reactive.common.dto.response.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
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

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(e: ServerWebInputException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.BAD_REQUEST.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadAmountOfMoneyException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handle(e: BadAmountOfMoneyException): ResponseEntity<Response> {
        val response = Response(
            status = HttpStatus.BAD_REQUEST.value(),
            message = e.message
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
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