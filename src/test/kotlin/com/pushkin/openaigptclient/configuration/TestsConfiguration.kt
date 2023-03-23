package com.pushkin.openaigptclient.configuration

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableAutoConfiguration
class TestsConfiguration {

    @Bean
    fun restTemplate() = RestTemplate()

}
