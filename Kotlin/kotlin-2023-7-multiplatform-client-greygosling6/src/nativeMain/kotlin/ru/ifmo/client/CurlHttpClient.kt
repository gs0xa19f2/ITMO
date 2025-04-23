@file:OptIn(ExperimentalForeignApi::class, ExperimentalCoroutinesApi::class)

package ru.ifmo.client

import kotlinx.cinterop.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import libcurl.*
import platform.posix.size_t

@OptIn(ExperimentalForeignApi::class)
private val curlGlobalInit: Int = curl_global_init(CURL_GLOBAL_ALL.convert()).convert()

@OptIn(ExperimentalForeignApi::class)
class CurlHttpClient : HttpClient {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun request(
        method: HttpMethod,
        request: HttpRequest,
    ): HttpResponse {
        val curl = curl_easy_init() ?: throw CurlException("curl_easy_init() failed")
        try {
            val headerData = mutableListOf<String>()
            val responseData = mutableListOf<Byte>()

            memScoped {
                val url = request.url.cstr.ptr
                curl_easy_setopt(curl, CURLOPT_URL, url)

                when (method) {
                    HttpMethod.GET ->
                        curl_easy_setopt(
                            curl,
                            CURLOPT_HTTPGET,
                            1,
                        )
                    HttpMethod.POST -> {
                        curl_easy_setopt(curl, CURLOPT_POST, 1L.toInt())
                        request.body?.let { body ->
                            curl_easy_setopt(
                                curl,
                                CURLOPT_POSTFIELDS,
                                body.refTo(0),
                            )
                            curl_easy_setopt(
                                curl,
                                CURLOPT_POSTFIELDSIZE,
                                body.size.toLong(),
                            )
                        }
                    }
                    HttpMethod.PUT -> {
                        curl_easy_setopt(
                            curl,
                            CURLOPT_CUSTOMREQUEST,
                            "PUT".cstr.ptr,
                        )
                        request.body?.let { body ->
                            curl_easy_setopt(
                                curl,
                                CURLOPT_POSTFIELDS,
                                body.refTo(0),
                            )
                            curl_easy_setopt(
                                curl,
                                CURLOPT_POSTFIELDSIZE,
                                body.size.toLong(),
                            )
                        }
                    }
                    HttpMethod.DELETE ->
                        curl_easy_setopt(
                            curl,
                            CURLOPT_CUSTOMREQUEST,
                            "DELETE".cstr.ptr,
                        )
                }

                val slist =
                    request.headers.value.asIterable().fold(
                        null as CPointer<curl_slist>?,
                    ) { acc, (key, value) ->
                        val headerStr = "$key: $value"
                        curl_slist_append(acc, headerStr)
                    }
                if (slist != null) {
                    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, slist)
                }

                val writeData =
                    staticCFunction {
                            ptr: CPointer<ByteVar>?,
                            size: size_t,
                            nmemb: size_t,
                            userdata: COpaquePointer?,
                        ->
                        val buffer = ptr?.readBytes((size * nmemb).toInt()) ?: return@staticCFunction 0uL
                        if (userdata != null) {
                            val userResponseData = userdata.asStableRef<MutableList<Byte>>().get()
                            userResponseData.addAll(buffer.toList())
                        }
                        size * nmemb
                    }

                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeData)
                curl_easy_setopt(
                    curl,
                    CURLOPT_WRITEDATA,
                    StableRef.create(responseData).asCPointer(),
                )

                val result = curl_easy_perform(curl)
                if (result != CURLE_OK) {
                    throw CurlException(
                        "curl_easy_perform() failed with code $result",
                    )
                }

                val responseCode =
                    memScoped {
                        val codeVar = alloc<LongVar>()
                        curl_easy_getinfo(
                            curl,
                            CURLINFO_RESPONSE_CODE,
                            codeVar.ptr,
                        )
                        codeVar.value
                    }

                return HttpResponse(
                    status = HttpStatus(responseCode.toInt()),
                    headers =
                        HttpHeaders(
                            headerData.associate {
                                val parts = it.split(": ", limit = 2)
                                parts[0] to parts[1]
                            },
                        ),
                    body = responseData.toByteArray(),
                )
            }
        } finally {
            curl_easy_cleanup(curl)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun close() {
        // Код для закрытия клиента, если это необходимо
    }
}

class CurlException(message: String) : RuntimeException(message)
