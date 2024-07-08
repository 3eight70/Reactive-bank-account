package com.learning.reactive.repository

import com.learning.reactive.models.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, UUID> {
    fun findByUsername(username: String): Optional<User>
}