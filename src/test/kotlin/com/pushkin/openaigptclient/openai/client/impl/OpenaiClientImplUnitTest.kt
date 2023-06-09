package com.pushkin.openaigptclient.openai.client.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushkin.openaigptclient.configuration.TestsConfiguration
import com.pushkin.openaigptclient.openai.exception.OpenaiClientException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withException
import org.springframework.web.client.RestTemplate
import java.io.IOException


@SpringBootTest(classes = [TestsConfiguration::class])
class OpenaiClientImplUnitTest {

    private lateinit var openaiClient: OpenaiClientImpl

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var openaiClientProperties: OpenaiClientProperties

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        openaiClient = OpenaiClientImpl(restTemplate, objectMapper, openaiClientProperties)
    }

    @Test
    fun fetchCompletionTestServerException() {
        mockServer.expect(
            ExpectedCount.times(3),
            requestTo("https://api.openai.com/v1/chat/completions")
        ).andRespond(withException(IOException("timeout")))
        assertThrows<OpenaiClientException> { openaiClient.fetchCompletion("test", 10) {} }
        mockServer.verify()
    }
}
