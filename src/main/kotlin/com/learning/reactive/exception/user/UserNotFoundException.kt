package com.learning.reactive.exception.user

class UserNotFoundException(username: String)
    : RuntimeException("Пользователь с именем $username не найден")