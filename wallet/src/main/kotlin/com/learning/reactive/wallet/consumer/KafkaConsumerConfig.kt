package com.learning.reactive.wallet.consumer

import com.learning.reactive.wallet.dto.deposit.DepositDto
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.util.*

@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val kafkaBootstrapServers: String
) {
    @Bean
    fun consumerProps(): Properties {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaBootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        props[ConsumerConfig.GROUP_ID_CONFIG] = "deposit"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        return props
    }

    @Bean
    fun kafkaConsumer(consumerProps: Properties): KafkaConsumer<String, DepositDto> {
        val deserializer = JsonDeserializer(DepositDto::class.java).apply {
            addTrustedPackages("*")
        }
        return KafkaConsumer(consumerProps, StringDeserializer(), deserializer)
    }

    @Bean
    fun newTopic(): NewTopic {
        return NewTopic(
            "deposit-topic",
            1,
            1
        )
    }
}