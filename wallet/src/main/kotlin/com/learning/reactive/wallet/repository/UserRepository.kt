package com.learning.reactive.wallet.repository

import com.learning.reactive.wallet.models.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByLogin(login: String): User?
    fun findByEmail(email: String): User?
}