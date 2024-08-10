package com.learning.reactive.wallet

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection("postgres")
	fun postgresContainer(): PostgreSQLContainer<*> {
		return PostgreSQLContainer(DockerImageName.parse("postgres:14-alpine"))
	}

	@Bean
	@ServiceConnection("kafka")
	fun kafkaContainer(): KafkaContainer {
		return KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"))
	}

	@Bean
	@ServiceConnection("redis")
	fun redisContainer(): GenericContainer<*> {
		return GenericContainer(DockerImageName.parse("redis:7-alpine"))
			.withExposedPorts(6379)
	}
}
