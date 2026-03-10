package com.prody.prashant.data.ai.mock

import java.util.ArrayDeque
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class ReplayableMockAiServer {
    private val server = MockWebServer()

    fun start(pathResponses: Map<String, List<MockResponse>>) {
        val queues = pathResponses.mapValues { ArrayDeque(it.value) }
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val queue = queues[request.path] ?: return MockResponse().setResponseCode(404)
                return queue.removeFirstOrNull() ?: MockResponse().setResponseCode(410)
            }
        }
        server.start()
    }

    fun url(path: String): String = server.url(path).toString()

    fun shutdown() = server.shutdown()
}
