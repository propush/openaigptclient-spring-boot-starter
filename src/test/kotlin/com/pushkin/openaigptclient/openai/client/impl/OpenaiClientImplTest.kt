package com.pushkin.openaigptclient.openai.client.impl

import com.pushkin.openaigptclient.configuration.TestsConfiguration
import com.pushkin.openaigptclient.openai.client.OpenaiClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf

@SpringBootTest(classes = [TestsConfiguration::class])
@EnabledIf(value = "#{'\${spring.profiles.active}' == 'inttest'}", loadContext = true)
class OpenaiClientImplTest {

    @Autowired
    private lateinit var openaiClient: OpenaiClient

    @Test
    fun fetchCompletion() {
        val prompt = "Say This is a test"
        val completion = openaiClient.fetchCompletion(prompt, 7) {
            println("completionChunk: $it")
        }
        assertTrue(completion.length > 7)
    }

    @Test
    fun fetchCompletionLong() {
        var chunkCounter = 0
        var finalChunk = -1
        val prompt = "What can you do?"
        val completion = openaiClient.fetchCompletion(prompt, 30) {
            println("completionChunk: $it")
            chunkCounter++
            if (!it.hasNext) {
                finalChunk = chunkCounter
            }
        }
        println("chunks: $chunkCounter, completion: $completion")
        assertTrue(completion.isNotBlank() && completion.length > 30)
        assertTrue(chunkCounter > 0)
        assertEquals(chunkCounter, finalChunk)
    }
}
