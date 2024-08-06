package com.learning.reactive.repository.reactive

import com.learning.reactive.models.Transaction
import com.learning.reactive.repository.TransactionRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Repository
class ReactiveTransactionRepository(
    private val transactionRepository: TransactionRepository
) {
    fun save(transaction: Transaction): Mono<Transaction> {
        return Mono.fromCallable {
            transactionRepository.save(transaction)
        }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun findById(id: UUID): Mono<Transaction> {
        return Mono.fromCallable {
            transactionRepository.findById(id)
                .orElse(null)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap {
                if (it != null) Mono.just(it) else Mono.empty()
            }
    }

    fun findAllByAccountId(accountId: UUID): Flux<Transaction> {
        return Flux.fromIterable(
            transactionRepository.findAllByAccountId(accountId)
        )
            .subscribeOn(Schedulers.boundedElastic())
    }
}