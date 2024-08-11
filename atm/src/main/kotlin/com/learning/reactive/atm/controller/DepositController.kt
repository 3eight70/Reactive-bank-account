package com.learning.reactive.atm.controller

import com.learning.reactive.atm.dto.DepositResponseDto
import com.learning.reactive.atm.exception.deposit.BadAmountOfMoneyException
import com.learning.reactive.atm.producer.KafkaProducer
import com.learning.reactive.common.dto.DepositDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal

/**
 * Контроллер, отвечающий за депозит средств
 */
@RestController
@RequestMapping("/api/v1/deposit")
class DepositController(
    private val kafkaProducer: KafkaProducer
) {
    /**
     * Пополнение средств
     *
     * @return Mono<DepositDto> - dto депозита
     */
    @PostMapping
    fun depositMoney(
        @RequestBody @Valid deposit: DepositDto
    ): Mono<DepositResponseDto> {
        if (deposit.amount <= BigDecimal.ZERO) {
            return Mono.error(BadAmountOfMoneyException())
        }

        return kafkaProducer.sendMessage(deposit)
            .thenReturn(
                DepositResponseDto(
                    "Запрос на пополнение средств на счет ${deposit.accountId} отправлен"
                )
            )
    }
}