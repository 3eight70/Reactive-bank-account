package com.learning.reactive.dto.user

import java.util.*

data class AuthResponseDto (
    private val userId: UUID,
    private val token: String,
    private val issuedAt: Date,
    private val expiresAt: Date
)