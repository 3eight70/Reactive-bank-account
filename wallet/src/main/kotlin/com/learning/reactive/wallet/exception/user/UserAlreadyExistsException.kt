package com.learning.reactive.wallet.exception.user

import com.learning.reactive.wallet.exception.common.BadRequestException

class UserAlreadyExistsException(message: String) : BadRequestException(message)