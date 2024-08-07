package com.learning.reactive.wallet.consumer

import com.learning.reactive.wallet.dto.deposit.DepositDto
import com.learning.reactive.wallet.service.AccountService
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.stereotype.Component

@Component
class ReactiveKafkaConsumer(
    private val accountService: AccountService,
    private val consumerFactory: ConsumerFactory<String, DepositDto>
) {
}