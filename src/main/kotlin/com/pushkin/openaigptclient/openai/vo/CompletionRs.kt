package com.pushkin.openaigptclient.openai.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

sealed class CompletionResponse {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class CompletionRs(
        val choices: List<Choice>,
        val created: Int,
        val id: String,
        val model: String,

        @JsonProperty("object")
        val theObject: String
    ) : CompletionResponse() {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Choice(
            val delta: Delta?,

            @JsonProperty("finish_reason")
            val finishReason: String?,

            val index: Int
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Delta(
            val content: String?
        )

    }

    object CompletionDone : CompletionResponse()

}


