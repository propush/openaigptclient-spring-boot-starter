package com.pushkin.openaigptclient.openai.vo

/**
 * A chunk of data returned by OpenAI API.
 *
 * @param text text of the chunk
 * @param hasNext whether there is more data to come
 *
 * @author Sergey Poziturin
 */
data class CompletionChunk(
    val text: String,
    val hasNext: Boolean
)
