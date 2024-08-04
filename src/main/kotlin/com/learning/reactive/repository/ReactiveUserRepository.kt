package com.learning.reactive.repository

import com.learning.reactive.models.User
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Repository
class ReactiveUserRepository(private val userRepository: UserRepository) {
    fun findByLogin(login: String): Mono<User> {
        return Mono.fromCallable {
            userRepository.findByLogin(login)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap {
                if (it != null) Mono.just(it) else Mono.empty()
            }
    }

    fun findById(id: UUID): Mono<User> {
        return Mono.fromCallable {
            userRepository.findById(id)
                .orElse(null)
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap {
                if (it != null) Mono.just(it) else Mono.empty()
            }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap {
                if (it != null) Mono.just(it) else Mono.empty()
            }
    }

    @Transactional
    fun save(user: User): Mono<User> {
        return Mono.fromCallable {
            userRepository.save(user)
        }
            .subscribeOn(Schedulers.boundedElastic())
    }
}