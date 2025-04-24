package ru.ifmo.client

import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.fetch.Headers
import kotlin.js.Promise

external fun require(module: String): dynamic

class NodeFetchClient : HttpClient {
    private val fetch: (
        url: String,
        init: dynamic,
    ) -> Promise<dynamic>?

    init {
        val nodeFetch = require("node-fetch")
        fetch = nodeFetch as? ((String, dynamic) -> Promise<dynamic>)
            ?: throw IllegalStateException("node-fetch module not found or invalid")
    }

    override suspend fun request(
        method: HttpMethod,
        request: HttpRequest,
    ): HttpResponse {
        val bodyArrayBuffer =
            request.body?.let {
                Uint8Array(
                    it.toTypedArray(),
                ).buffer
            }
        val headers = Headers()
        request.headers.value.forEach { (key, value) ->
            headers.append(key, value)
        }
        val requestOptions =
            jsObject {
                this.method = method.name
                this.headers = headers
                this.body = bodyArrayBuffer
            }

        val responsePromise = fetch.invoke(request.url, requestOptions)
        val response = responsePromise?.await()
            ?: throw IllegalStateException("Fetch request failed or fetch is not available")

        val arrayBuffer: ArrayBuffer = (response.arrayBuffer() as Promise<ArrayBuffer>).await()
        val uint8Array = Uint8Array(arrayBuffer)

        val byteArray =
            ByteArray(uint8Array.length) { index -> uint8Array[index] }

        val headersMap = mutableMapOf<String, String>()
        val headersObj = response.headers
        val keysIterator: dynamic = headersObj.keys()
        var key: dynamic = keysIterator.next()

        while (!key.done as Boolean) {
            val headerValue: String = headersObj.get(key.value).toString()
            headersMap[key.value as String] = headerValue
            key = keysIterator.next()
        }

        return HttpResponse(
            status = HttpStatus(response.status as Int),
            headers = HttpHeaders(headersMap),
            body = byteArray,
        )
    }

    override fun close() {
        // Код для закрытия клиента, если это необходимо
    }

    private fun jsObject(init: dynamic.() -> Unit): dynamic {
        val obj = js("{}")
        init(obj)
        return obj
    }
}
