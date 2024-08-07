package com.learning.reactive.atm.controller

import com.learning.reactive.atm.dto.DepositDto
import com.learning.reactive.atm.dto.DepositResponseDto
import com.learning.reactive.atm.service.DepositService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Контроллер, отвечающий за депозит средств
 */
@RestController
@RequestMapping("/api/v1/deposit")
class DepositController(
    private val depositService: DepositService
) {
    /**
     * Пополнение средств
     *
     * @return Mono<DepositDto> - dto депозита
     */
    @PostMapping
    fun depositMoney(
        @RequestBody deposit: DepositDto
    ): DepositResponseDto {
        return depositService.depositMoney(deposit)
    }
}