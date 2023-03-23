package com.pushkin.openaigptclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushkin.openaigptclient.openai.client.OpenaiClient
import com.pushkin.openaigptclient.openai.client.impl.OpenaiClientImpl
import com.pushkin.openaigptclient.openai.client.impl.OpenaiClientProperties
import com.pushkin.openaigptclient.openai.service.OpenaiService
import com.pushkin.openaigptclient.openai.service.impl.OpenaiServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@EnableConfigurationProperties(OpenaiClientProperties::class)
@AutoConfiguration
@SpringBootConfiguration
class OpenaiGptClientAutoConfiguration {

    @Autowired
    private lateinit var openaiClientProperties: OpenaiClientProperties

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Bean
    @ConditionalOnMissingBean(OpenaiClient::class)
    fun openaiClient(): OpenaiClient = OpenaiClientImpl(
        restTemplate,
        objectMapper,
        openaiClientProperties
    )

    @Bean
    @ConditionalOnMissingBean(OpenaiService::class)
    fun openaiService(): OpenaiService = OpenaiServiceImpl(openaiClient())

}
