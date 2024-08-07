package com.learning.reactive.wallet.consumer

import com.learning.reactive.wallet.dto.deposit.DepositDto
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val kafkaBootstrapServers: String
) {
    @Bean
    fun consumerConfigs(): Map<String, Any> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to true
        )

        return configProps
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, DepositDto> {
        return DefaultKafkaConsumerFactory(
            consumerConfigs(),
            StringDeserializer(),
            ErrorHandlingDeserializer(JsonDeserializer(DepositDto::class.java))
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, DepositDto> {
        return ConcurrentKafkaListenerContainerFactory<String, DepositDto>()
    }

    @Bean
    fun singleFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, DepositDto>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, DepositDto>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = false

        return factory
    }

    @Bean
    fun converter(): StringJsonMessageConverter {
        return StringJsonMessageConverter()
    }

    @Bean
    fun batchFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, DepositDto>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, DepositDto>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = true
        factory.setBatchMessageConverter(BatchMessagingMessageConverter(converter()))

        return factory
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