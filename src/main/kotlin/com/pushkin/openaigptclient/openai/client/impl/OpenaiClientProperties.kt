package com.pushkin.openaigptclient.openai.client.impl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "openai")
data class OpenaiClientProperties @ConstructorBinding constructor(
    var baseUrl: String = BASE_URL_V1,
    val apiKey: String,
    val organization: String,
    val retryCount: Int
) {
    companion object {
        const val BASE_URL_V1 = "https://api.openai.com/v1"
    }
}
