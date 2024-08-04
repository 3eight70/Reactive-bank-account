package com.learning.reactive.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "t_transactions")
data class Transaction (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @Column(name = "account_id_from", nullable = false)
    val accountIdFrom: UUID,

    @Column(name = "account_id_where", nullable = false)
    val accountIdWhere: UUID,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @Column(name = "transaction_time", nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)