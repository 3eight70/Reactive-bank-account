package com.learning.reactive.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

/**
 * Счет
 */
@Entity
@Table(name = "t_accounts")
class Account (
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    /**
     * Идентификатор владельца счета
     */
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    /**
     * Баланс счета
     */
    @Column(name = "balance", nullable = false)
    val balance: BigDecimal
)