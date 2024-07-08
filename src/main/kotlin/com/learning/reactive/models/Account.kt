package com.learning.reactive.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

/**
 * Счет
 */
@Entity
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
    @Column(name = "userId", nullable = false)
    val userId: UUID,

    /**
     * Баланс счета
     */
    @Column(name = "balance", nullable = false)
    val balance: BigDecimal
)