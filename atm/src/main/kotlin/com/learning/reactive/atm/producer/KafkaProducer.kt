package com.learning.reactive.atm.producer

import com.learning.reactive.common.dto.DepositDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import reactor.core.scheduler.Schedulers
import java.time.Duration

@Component
class KafkaProducer(
    @Value("\${application.retry}")
    private val retry: Long,
    @Value("\${application.timeout}")
    private val timeout: Long,
    private val kafkaTemplate: KafkaTemplate<String, DepositDto>
) {
    private val DEPOSIT_TOPIC = "deposit-topic"

    fun sendMessage(depositDto: DepositDto): Mono<Void> {
        return Mono.create { sink: MonoSink<Void> ->
            kafkaTemplate.send(DEPOSIT_TOPIC, depositDto)
                .thenApply {
                    sink.success()
                }
                .exceptionally {
                    sink.error(it)
                    null
                }
        }
            .subscribeOn(Schedulers.boundedElastic())
            .retry(retry)
            .timeout(Duration.ofSeconds(timeout))
    }
}