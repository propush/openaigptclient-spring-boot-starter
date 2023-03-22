package com.pushkin.openaigptclient.openai.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class CompletionRq(

    val messages: List<Message>,

    val model: String,

    val stream: Boolean,

    @JsonProperty("max_tokens")
    val maxTokens: Int,

    val temperature: Int
) {

    data class Message(
        val role: String?,
        val content: String
    )

}
