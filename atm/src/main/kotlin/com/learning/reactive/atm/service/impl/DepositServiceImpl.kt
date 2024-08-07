package com.learning.reactive.atm.service.impl

import com.learning.reactive.atm.dto.DepositDto
import com.learning.reactive.atm.dto.DepositResponseDto
import com.learning.reactive.atm.service.DepositService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DepositServiceImpl : DepositService {
    override fun depositMoney(deposit: DepositDto): DepositResponseDto {
        TODO("Not yet implemented")
    }
}