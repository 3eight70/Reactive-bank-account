package com.learning.reactive.wallet.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * Транзакция
 */
@Entity
@Table(name = "t_transactions")
data class Transaction(
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    /**
     * Идентификатор счета, откуда списываются средства,
     * может быть null, в случае, если происходит пополнение из банкомата
     */
    @Column(name = "account_id_from", nullable = false)
    val accountIdFrom: UUID?,

    /**
     * Идентификатор счета, куда начисляются средства
     */
    @Column(name = "account_id_where", nullable = false)
    val accountIdWhere: UUID,

    /**
     * Сумма средств
     */
    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    /**
     * Время совершения транзакции
     */
    @Column(name = "transaction_time", nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)