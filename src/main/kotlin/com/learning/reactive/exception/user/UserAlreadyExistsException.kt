package com.learning.reactive.exception.user

import com.learning.reactive.exception.common.BadRequestException

class UserAlreadyExistsException(message: String) : BadRequestException(message)