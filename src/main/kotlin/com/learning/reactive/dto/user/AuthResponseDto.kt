package com.learning.reactive.dto.user

import java.util.*

data class AuthResponseDto(
    val userId: UUID,
    val token: String,
    val issuedAt: Date,
    val expiresAt: Date
)