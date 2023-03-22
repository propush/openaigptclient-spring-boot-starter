package com.pushkin.openaigptclient.openai.client

import com.pushkin.openaigptclient.openai.vo.CompletionChunk

interface OpenaiClient {

    fun fetchCompletion(
        prompt: String,
        maxTokens: Int,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit
    ): String

}
