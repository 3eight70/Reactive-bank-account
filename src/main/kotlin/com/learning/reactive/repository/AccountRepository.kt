package com.learning.reactive.repository

import com.learning.reactive.models.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findAllByUserId(userId: UUID): List<Account>
    fun findAccountById(id: UUID): Account?
}