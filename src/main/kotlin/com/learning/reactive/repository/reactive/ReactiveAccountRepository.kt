package com.learning.reactive.repository.reactive

import com.learning.reactive.models.Account
import com.learning.reactive.repository.AccountRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Repository
class ReactiveAccountRepository(
    private val accountRepository: AccountRepository
) {
    fun save(account: Account): Mono<Account> {
        return Mono.fromCallable {
            accountRepository.save(account)
        }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun findAllByUserId(userId: UUID): Flux<Account> {
        return Flux.fromIterable(
            accountRepository.findAllByUserId(userId)
        )
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun findById(id: UUID): Mono<Account> {
        return Mono.fromCallable {
            accountRepository.findById(id)
                .orElse(null)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap {
                if (it != null) Mono.just(it) else Mono.empty()
            }
    }
}