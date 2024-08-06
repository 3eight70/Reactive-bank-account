package com.learning.reactive.exception.user

import com.learning.reactive.exception.common.NotFoundException

class UserNotFoundException(username: String) : NotFoundException("Пользователь с именем $username не найден")