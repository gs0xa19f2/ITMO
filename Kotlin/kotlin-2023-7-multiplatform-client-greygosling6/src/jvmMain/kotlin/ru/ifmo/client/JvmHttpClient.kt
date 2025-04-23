package ru.ifmo.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient as JavaHttpClient
import java.net.http.HttpRequest as JavaHttpRequest
import java.net.http.HttpResponse as JavaHttpResponse

class JvmHttpClient : HttpClient {
    private val client = JavaHttpClient.newBuilder().build()

    override suspend fun request(
        method: HttpMethod,
        request: HttpRequest,
    ): HttpResponse {
        val builder =
            JavaHttpRequest.newBuilder()
                .uri(URI.create(request.url))

        when (method) {
            HttpMethod.GET -> builder.GET()
            HttpMethod.POST ->
                request.body?.let {
                    builder.POST(JavaHttpRequest.BodyPublishers.ofByteArray(it))
                } ?: JavaHttpRequest.BodyPublishers.noBody()
            HttpMethod.PUT ->
                request.body?.let {
                    builder.PUT(JavaHttpRequest.BodyPublishers.ofByteArray(it))
                }  ?: JavaHttpRequest.BodyPublishers.noBody()
            HttpMethod.DELETE -> builder.DELETE()
        }

        request.headers.value.forEach { (name, value) ->
            builder.header(name, value)
        }

        val response =
            withContext(Dispatchers.IO) {
                client.sendAsync(
                    builder.build(),
                    JavaHttpResponse.BodyHandlers.ofByteArray(),
                ).await()
            }
        return HttpResponse(
            status = HttpStatus(response.statusCode()),
            headers =
                HttpHeaders(
                    response.headers().map().mapValues { it.value[0] },
                ),
            body = response.body(),
        )
    }

    override fun close() {
        // Код для закрытия клиента, если это необходимо
    }
}
