package com.learning.reactive.wallet.config

import com.learning.reactive.wallet.dto.account.AccountBalanceDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val hostname: String,
    @Value("\${spring.data.redis.port}")
    private val port: Int
) {
    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration(hostname, port)

        return LettuceConnectionFactory(config)
    }

    @Bean
    fun redisTemplate(): ReactiveRedisTemplate<String, AccountBalanceDto> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = Jackson2JsonRedisSerializer(AccountBalanceDto::class.java)

        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, AccountBalanceDto>(keySerializer)
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(lettuceConnectionFactory(), serializationContext)
    }
}