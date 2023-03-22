package com.pushkin.openaigptclient.openai.service

import com.pushkin.openaigptclient.openai.vo.CompletionChunk

interface OpenaiService {

    fun query(
        prompt: String,
        maxTokens: Int,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit
    ): String

}
