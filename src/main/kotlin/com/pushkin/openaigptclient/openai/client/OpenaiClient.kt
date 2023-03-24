package com.pushkin.openaigptclient.openai.client

import com.pushkin.openaigptclient.openai.exception.OpenaiClientException
import com.pushkin.openaigptclient.openai.service.OpenaiService
import com.pushkin.openaigptclient.openai.vo.CompletionChunk

/**
 * NOTE: Consider using [OpenaiService] as this client is a low level one.
 * OpenAI client interface
 *
 * @author Sergey Poziturin
 */
interface OpenaiClient {

    /**
     * NOTE: Consider using [OpenaiService.query] instead of this method.
     * Fetch chat completion from OpenAI API.
     * Completion is generated in chunks with the streaming call and each chunk is passed to the callback provided.
     *
     * @param prompt prompt text
     * @param maxTokens maximum number of tokens to generate in response
     * @param chunkCallback callback to receive completion chunks
     *
     * @return full completion text

     * @throws OpenaiClientException if OpenAI API returns error or response was not received
     *
     * @author Sergey Poziturin
     */
    fun fetchCompletion(
        prompt: String,
        maxTokens: Int,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit
    ): String

}
