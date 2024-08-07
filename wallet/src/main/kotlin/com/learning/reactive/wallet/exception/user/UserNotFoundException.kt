package com.learning.reactive.wallet.exception.user

import com.learning.reactive.wallet.exception.common.NotFoundException

class UserNotFoundException(username: String) : NotFoundException("Пользователь с именем $username не найден")