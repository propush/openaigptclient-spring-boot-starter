package com.pushkin.openaigptclient.openai.client.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushkin.openaigptclient.openai.client.OpenaiClient
import com.pushkin.openaigptclient.openai.exception.OpenaiClientException
import com.pushkin.openaigptclient.openai.vo.CompletionChunk
import com.pushkin.openaigptclient.openai.vo.CompletionResponse
import com.pushkin.openaigptclient.openai.vo.CompletionRq
import mu.KLogging
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate


class OpenaiClientImpl(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val openaiClientProperties: OpenaiClientProperties
) : OpenaiClient, KLogging() {

    companion object {
        private const val DEFAULT_MODEL = "gpt-3.5-turbo-0301"
        private const val DEFAULT_ROLE = "user"
        private const val BUFFER_SIZE = 1024

        //        private const val DEFAULT_MODEL = "text-davinci-003"
        private const val BASE_URL_V1 = "https://api.openai.com/v1"
        private const val COMPLETION_PATH = "/chat/completions"

        private val dataRegex = Regex(
            "^[\\n\\s]*data: \\s*(.*)[\\n\\s]*\$",
            setOf(RegexOption.MULTILINE, RegexOption.UNIX_LINES)
        )
    }

    override fun fetchCompletion(
        prompt: String,
        maxTokens: Int,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit
    ): String {
        val rq = RequestCallback { prepareCompletionHttpRequest(it, prompt, maxTokens) }
        val rs = StringBuffer()
        var retryCount = 0
        var gotResponse = false
        do {
            try {
                retryCount++
                logger.info { "Fetching completion for prompt, try $retryCount of ${openaiClientProperties.retryCount}: $prompt" }
                val responseExtractor: (response: ClientHttpResponse) -> Unit =
                    { response -> processResponseBody(response, rs, chunkCallback) }
                restTemplate.execute(
                    "$BASE_URL_V1$COMPLETION_PATH",
                    HttpMethod.POST,
                    rq,
                    responseExtractor,
                    null
                )
                gotResponse = true
                logger.info { "Got response: ${rs.length} chars" }
            } catch (e: ResourceAccessException) {
                logger.warn { "Retrying..." }
            } catch (e: Exception) {
                throw OpenaiClientException("Error while fetching completion", e)
            }
        } while (retryCount < openaiClientProperties.retryCount && !gotResponse)
        if (!gotResponse) {
            throw OpenaiClientException("Sorry, I'm not able to answer your question right now. Please try again later.")
        }
        return rs.toString()
    }

    private fun processResponseBody(
        response: ClientHttpResponse,
        rs: StringBuffer,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit
    ) {
        logger.debug { "Response: ${response.statusCode}" }
        if (response.statusCode != HttpStatus.OK) {
            throw OpenaiClientException("Bad response code while fetching completion: ${response.statusCode}")
        }
        val sb = StringBuffer(BUFFER_SIZE)
        val bb = ByteArray(BUFFER_SIZE)
        var byteIndex = 0
        response.body.use { s ->
            do {
                val byteRead = s.read()
                if (byteRead >= 0 && byteIndex < BUFFER_SIZE) {
                    bb[byteIndex++] = byteRead.toByte()
                    if (byteRead == 10) {
                        sb.append(String(bb, 0, byteIndex))
                        byteIndex = 0
                        chunkReceived(sb, rs, false, chunkCallback)
                    }
                } else {
                    sb.append(String(bb, 0, byteIndex))
                    byteIndex = 0
                    chunkReceived(sb, rs, true, chunkCallback)
                }
            } while (byteRead >= 0)
        }
    }

    private fun chunkReceived(
        source: StringBuffer,
        target: StringBuffer,
        lastChunk: Boolean,
        chunkCallback: (completionChunk: CompletionChunk) -> Unit,
    ) {
        logger.debug { "Chunk received: $source" }
        val body = source.toString()
        source.delete(0, source.length)
        if (body.isBlank()) {
            return
        }
        parseCompletionEvent(body).let { completionResponse ->
            when (completionResponse) {
                is CompletionResponse.CompletionDone -> {
                    logger.debug { "Completion done" }
                    chunkCallback(CompletionChunk("", false))
                }

                is CompletionResponse.CompletionRs -> {
                    logger.debug { "Completion response: ${completionResponse.id}" }
                    val choices = completionResponse.choices.joinToString { it.delta?.content ?: "" }
                    target.append(choices)
                    chunkCallback(CompletionChunk(choices, !lastChunk))
                }
            }
        }
    }

    private fun prepareCompletionHttpRequest(
        clientHttpRequest: ClientHttpRequest,
        prompt: String,
        maxTokens: Int
    ) {
        prepareBaseHttpRequest(clientHttpRequest)
        val rqBody = objectMapper.writeValueAsBytes(defaultCompletion(prompt, maxTokens))
        clientHttpRequest.body.write(rqBody)
        clientHttpRequest.body.flush()
    }

    private fun prepareBaseHttpRequest(clientHttpRequest: ClientHttpRequest) {
        clientHttpRequest.headers.setBearerAuth(openaiClientProperties.apiKey)
        clientHttpRequest.headers.contentType = MediaType.APPLICATION_JSON
        clientHttpRequest.headers.accept = listOf(MediaType.APPLICATION_JSON)
        if (openaiClientProperties.organization.isNotEmpty()) {
            clientHttpRequest.headers.set("OpenAI-Organization", openaiClientProperties.organization)
        }
    }

    private fun parseCompletionEvent(body: String): CompletionResponse {
        if (!body.matches(dataRegex)) {
            throw OpenaiClientException("Invalid response received (should start with 'data:': $body")
        }
        val dataSection = body.replace(dataRegex, "\$1")
        if (dataSection == "[DONE]") {
            return CompletionResponse.CompletionDone
        }
        return objectMapper.readValue(dataSection, CompletionResponse.CompletionRs::class.java)
    }

    private fun defaultCompletion(prompt: String, maxTokens: Int) = CompletionRq(
        messages = listOf(CompletionRq.Message(DEFAULT_ROLE, prompt)),
        model = DEFAULT_MODEL,
        stream = true,
        maxTokens = maxTokens,
        temperature = 0
    )
}
