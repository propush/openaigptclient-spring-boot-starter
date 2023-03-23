package com.pushkin.openaigptclient

import com.pushkin.openaigptclient.configuration.TestsConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TestsConfiguration::class])
class OpenaiGptClientApplicationTests {

    @Test
    fun contextLoads() {
    }

}
