package com.learning.reactive.atm.controller

import com.learning.reactive.atm.dto.DepositDto
import com.learning.reactive.atm.dto.DepositResponseDto
import com.learning.reactive.atm.producer.KafkaProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

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
        @RequestBody deposit: DepositDto
    ): Mono<DepositResponseDto> {
        return kafkaProducer.sendMessage(deposit)
            .thenReturn(
                DepositResponseDto(
                    "Запрос на пополнение средств на счет ${deposit.accountId} отправлен"
                )
            )
    }
}