package com.learning.reactive.service.impl

import com.learning.reactive.exceptions.user.UserNotFoundException
import com.learning.reactive.models.User
import com.learning.reactive.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(login: String): User {
        return userRepository.findByLogin(login)
            .orElseThrow{ UserNotFoundException(login) }
    }
}