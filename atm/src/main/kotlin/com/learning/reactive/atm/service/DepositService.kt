package com.learning.reactive.atm.service

import com.learning.reactive.atm.dto.DepositDto
import com.learning.reactive.atm.dto.DepositResponseDto

interface DepositService {
    fun depositMoney(deposit: DepositDto): DepositResponseDto
}