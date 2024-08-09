package com.learning.reactive.wallet.consumer

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class KafkaConsumerRunner(
    private val kafkaConsumer: ReactiveKafkaConsumer
) {
    @Bean
    fun depositProcessRunner(): ApplicationRunner {
        return ApplicationRunner {
            kafkaConsumer.receiveAndProcessMessages().subscribe()
        }
    }
}