package com.learning.reactive.repository.reactive

import com.learning.reactive.models.User
import com.learning.reactive.repository.UserRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

/**
 * Реактивная обертка-репозиторий для пользователя
 */
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

    fun findByEmail(email: String): Mono<User> {
        return Mono.fromCallable {
            userRepository.findByEmail(email)
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
    }

    fun save(user: User): Mono<User> {
        return Mono.fromCallable {
            userRepository.save(user)
        }
            .subscribeOn(Schedulers.boundedElastic())
    }
}