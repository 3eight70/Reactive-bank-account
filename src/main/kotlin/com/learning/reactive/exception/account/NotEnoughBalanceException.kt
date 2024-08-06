package com.learning.reactive.exception.account

import com.learning.reactive.exception.common.BadRequestException

class NotEnoughBalanceException : BadRequestException("На счете недостаточно средств") {
}