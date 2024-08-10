package com.learning.reactive.wallet.consumer

import com.learning.reactive.common.dto.DepositDto
import com.learning.reactive.wallet.service.AccountService
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration

@Component
class ReactiveKafkaConsumer(
    private val accountService: AccountService,
    private val kafkaConsumer: KafkaConsumer<String, DepositDto>
) {
    private val log = LoggerFactory.getLogger(ReactiveKafkaConsumer::class.java)
    private val DEPOSIT_TOPIC = "deposit-topic"

    fun receiveAndProcessMessages(): Flux<Void> {
        return Flux.create<DepositDto> { emitter ->
            kafkaConsumer.subscribe(listOf(DEPOSIT_TOPIC))

            while (!emitter.isCancelled) {
                val records = kafkaConsumer.poll(Duration.ofMillis(100))
                records.forEach { record ->
                    try {
                        emitter.next(record.value())

                        kafkaConsumer.commitSync()
                    } catch (e: Exception) {
                        emitter.error(e)
                    }
                }
            }

            kafkaConsumer.close()
            emitter.complete()
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { depositDto ->
                log.info("Депозит на счет ${depositDto.accountId} отправляется на обработку")
                accountService.processDeposit(depositDto)
            }
            .doOnError { error ->
                println("Error occurred: ${error.message}")
            }
    }
}