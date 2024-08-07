package com.learning.reactive.wallet.repository

import com.learning.reactive.wallet.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID> {
    fun findTransactionById(id: UUID): Transaction?
    @Query(value = """
        SELECT * FROM t_transactions AS t
        WHERE t.account_id_from = :accountId
        OR t.account_id_where = :accountId
    """, nativeQuery = true)
    fun findAllByAccountId(accountId: UUID): List<Transaction>
}