package ru.ifmo.client

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.fetch.RequestInit

class FetchClient : HttpClient {
    override suspend fun request(
        method: HttpMethod,
        request: HttpRequest,
    ): HttpResponse {
        val bodyArrayBuffer =
            request.body?.let {
                Int8Array(
                    it.toTypedArray(),
                ).buffer
            }
        val headers =
            jsObject {
                request.headers.value.forEach {
                        (key, value) ->
                    this[key] = value
                }
            }
        val requestOptions =
            RequestInit(
                method = method.name,
                headers = headers,
                body = bodyArrayBuffer,
            )
        val response = window.fetch(request.url, requestOptions).await()

        val buffer = response.arrayBuffer().await()
        val uint8Array = Uint8Array(buffer)
        val responseBody = ByteArray(uint8Array.length) { index -> uint8Array[index] }

        val headersMap = mutableMapOf<String, String>()
        response.headers.asDynamic().forEach { key: dynamic, value: dynamic ->
            headersMap.set(key.toString(), value.toString())
        }

        return HttpResponse(
            status = HttpStatus(response.status.toInt()),
            headers = HttpHeaders(headersMap),
            body = responseBody,
        )
    }

    private fun jsObject(init: dynamic.() -> Unit): dynamic {
        val obj = js("{}")
        init(obj)
        return obj
    }

    override fun close() {
        // Код для закрытия клиента, если это необходимо
    }
}
