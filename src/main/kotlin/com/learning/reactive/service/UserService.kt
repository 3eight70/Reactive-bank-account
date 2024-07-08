package com.learning.reactive.service

import com.learning.reactive.dao.user.LoginUserRequest
import com.learning.reactive.dao.user.RegisterUserRequest
import com.learning.reactive.models.User

interface UserService {
    fun registerUser(registerUserRequest: RegisterUserRequest)
    fun loginUser(loginUserRequest: LoginUserRequest)
    fun getProfile(user: User)
}