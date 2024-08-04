package com.learning.reactive.models

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

/**
 * Пользователь
 */
@Entity
@Table(name = "t_users")
class User (
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: UUID,

    /**
     * Логин
     */
    @Column(name = "login",unique = true, nullable = false)
    private val login: String,

    /**
     * Электронная почта
     */
    @Column(name = "email", unique = true, nullable = false)
    private val email: String,

    /**
     * Пароль
     */
    @Column(name = "password", nullable = false)
    private var passwordHash: String
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return login
    }

    fun getEmail(): String {
        return email
    }

    fun getId(): UUID {
        return id
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return passwordHash
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}