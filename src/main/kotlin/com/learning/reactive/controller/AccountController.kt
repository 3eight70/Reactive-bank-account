package com.learning.reactive.controller

import com.learning.reactive.service.AccountService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Контроллер, отвечающий за работу со счетами
 */
@RestController
@RequestMapping("/api/v1/account")
class AccountController(
    private val accountService: AccountService
) {
}