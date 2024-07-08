package com.learning.reactive.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun securityBeanPostProcessor(): ObjectPostProcessor<Any> {
        return SecurityBeanPostProcessor()
    }

    @Bean
    fun authenticationManagerBuilder(): AuthenticationManagerBuilder {
        return AuthenticationManagerBuilder(securityBeanPostProcessor())
    }

    private class SecurityBeanPostProcessor : ObjectPostProcessor<Any> {
        override fun <T : Any?> postProcess(objectInstance: T): T {
            return objectInstance
        }
    }

    @Bean
    fun corsConfiguration(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("*")
        corsConfiguration.allowedMethods = listOf("*")
        corsConfiguration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)

        return source
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.cors{cors -> cors.configurationSource(corsConfiguration())}
            .csrf{csrf -> csrf.disable()}
            .authorizeExchange{exchange -> exchange
                .anyExchange().authenticated()}
            .httpBasic{}
            .formLogin{}

        return http.build()
    }
}