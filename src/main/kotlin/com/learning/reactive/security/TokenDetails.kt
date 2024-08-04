package com.learning.reactive.security

import java.util.*

data class TokenDetails(
    val userId: UUID,
    val token: String,
    val issuedAt: Date,
    val expiresAt: Date
)