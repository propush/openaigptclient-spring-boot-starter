package com.pushkin.openaigptclient.openai.service

import com.pushkin.openaigptclient.openai.vo.CompletionChunk

/**
 * Service for requesting OpenAI API
 *
 * @author Sergey Poziturin
 */
interface OpenaiService {

    /**
     * Query OpenAI API for completion.
     * Completion response is streamed in complete text lines, each line is immediately passed to the
     * callback provided.
     *
     * @param prompt prompt text
     * @param maxTokens maximum number of tokens to generate in response
     * @param chunkCallback callback to receive completion chunks
     *
     * @return full completion text
     *
     * @author Sergey Poziturin
     */
    fun query(
        prompt: String,
        maxTokens: Int,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit
    ): String

}
