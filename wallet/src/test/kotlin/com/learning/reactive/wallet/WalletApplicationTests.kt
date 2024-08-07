package com.learning.reactive.wallet

import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class WalletApplicationTests {

	@Test
	fun contextLoads() {
	}

}
