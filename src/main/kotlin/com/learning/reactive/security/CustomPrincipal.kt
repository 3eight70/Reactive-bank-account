package com.learning.reactive.security

import java.security.Principal
import java.util.*

data class CustomPrincipal(
    private val id: UUID,
    private val login: String,
    private val email: String
) : Principal {
    override fun getName(): String {
        return login
    }

    fun getId(): UUID {
        return id
    }

    fun getEmail(): String {
        return email
    }
}