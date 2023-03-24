package com.pushkin.openaigptclient.openai.service.impl

import com.pushkin.openaigptclient.openai.client.OpenaiClient
import com.pushkin.openaigptclient.openai.exception.OpenaiClientException
import com.pushkin.openaigptclient.openai.vo.CompletionChunk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock


class OpenaiServiceImplTest {

    private lateinit var openaiServiceImpl: OpenaiServiceImpl
    private lateinit var openaiClient: OpenaiClient

    @Test
    fun query() {
        openaiClient = mock {
            on { fetchCompletion(any(), any(), any()) } doAnswer {
                with((it.getArgument(2) as ((CompletionChunk) -> Unit))) {
                    invoke(CompletionChunk("test1 ", true))
                    invoke(CompletionChunk("test2", true))
                    invoke(CompletionChunk("\n", true))
                    invoke(CompletionChunk("test3", true))
                    invoke(CompletionChunk("\n", true))
                    invoke(CompletionChunk("\n", true))
                    invoke(CompletionChunk("", false))
                }
                "test1 test2\ntest3\n\n"
            }
        }
        openaiServiceImpl = OpenaiServiceImpl(openaiClient)

        var chunkCounter = 0
        val rs = openaiServiceImpl.query("test", 10) {
            println(it)
            when (chunkCounter) {
                0 -> {
                    assertEquals("test1 test2", it.text)
                    assertTrue(it.hasNext)
                }

                1 -> {
                    assertEquals("test3", it.text)
                    assertTrue(it.hasNext)
                }

                2 -> {
                    assertEquals("", it.text)
                    assertFalse(it.hasNext)
                }
            }
            chunkCounter++
        }
        assertEquals("test1 test2\ntest3\n\n", rs)
        assertEquals(3, chunkCounter)
    }

    @Test
    fun queryExceptionInClient() {
        openaiClient = mock {
            on { fetchCompletion(any(), any(), any()) } doAnswer {
                throw OpenaiClientException("test")
            }
        }
        openaiServiceImpl = OpenaiServiceImpl(openaiClient)
        val rs = openaiServiceImpl.query("test", 10) {
            println(it)
            assertEquals(CompletionChunk("test", false), it)
        }
        assertEquals("test", rs)
    }
}
