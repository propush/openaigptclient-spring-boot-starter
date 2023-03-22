package com.pushkin.openaigptclient.openai.vo

data class CompletionChunk(
    val text: String,
    val hasNext: Boolean
)
