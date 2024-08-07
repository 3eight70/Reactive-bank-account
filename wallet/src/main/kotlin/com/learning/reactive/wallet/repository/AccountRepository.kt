package com.learning.reactive.wallet.repository

import com.learning.reactive.wallet.models.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findAllByUserId(userId: UUID): List<Account>
    fun findAccountById(id: UUID): Account?
}