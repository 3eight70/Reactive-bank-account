package com.learning.reactive.exceptions.user

class UserNotFoundException(username: String)
    : RuntimeException("Пользователь с именем $username не найден")