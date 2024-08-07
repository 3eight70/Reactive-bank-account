package com.learning.reactive.atm.producer

import com.learning.reactive.atm.dto.DepositDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, DepositDto>
) {
    private val DEPOSIT_TOPIC = "deposit-topic"

    fun sendMessage(depositDto: DepositDto): Mono<Void> {
        return Mono.create { sink: MonoSink<Void> ->
            kafkaTemplate.send(DEPOSIT_TOPIC, depositDto)
                .thenApply { sendResult: SendResult<String, DepositDto> ->
                    sink.success()
                }
                .exceptionally {
                    sink.error(it)
                    null
                }
        }
    }
}