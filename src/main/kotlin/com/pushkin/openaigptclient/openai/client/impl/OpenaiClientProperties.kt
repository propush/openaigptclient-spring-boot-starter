package com.pushkin.openaigptclient.openai.client.impl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "openai")
data class OpenaiClientProperties @ConstructorBinding constructor(
    val apiKey: String,
    val organization: String,
    val retryCount: Int
)
