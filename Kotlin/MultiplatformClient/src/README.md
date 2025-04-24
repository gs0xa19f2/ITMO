# Multiplatform HTTP Client: Каталог `src`

## Описание
Каталог `src` содержит исходный код мультиплатформенного HTTP-клиента. Включает общие интерфейсы и реализации для платформ JVM, JS и Native.

---

## Структура каталога

### Общий модуль (`commonMain`)
- Расположен в `commonMain/kotlin/ru/ifmo/client`.
- Основные файлы:
  - `HttpClient.kt`: Интерфейс HTTP-клиента.
  - `HttpRequest.kt`: Модель HTTP-запроса.
  - `HttpResponse.kt`: Модель HTTP-ответа.
  - `HttpHeaders.kt`: Заголовки HTTP.
  - `HttpMethod.kt`: Поддерживаемые HTTP-методы.
  - `HttpStatus.kt`: Коды статусов HTTP.

### Платформенные модули
#### JVM (`jvmMain`)
- Расположен в `jvmMain/kotlin/ru/ifmo/client`.
- Использует стандартный Java HTTP Client.
- Основные файлы:
  - `HttpClient.jvm.kt`: Ожидаемая реализация HTTP-клиента для JVM.
  - `JvmHttpClient.kt`: Обёртка над Java HTTP Client.

#### JS (`jsMain`)
- Расположен в `jsMain/kotlin/ru/ifmo/client`.
- Поддерживает две платформы:
  - **Browser**: Использует Fetch API.
  - **Node.js**: Использует node-fetch.
- Основные файлы:
  - `FetchClient.kt`: Реализация для браузеров.
  - `NodeFetchClient.kt`: Реализация для Node.js.

#### Native (`nativeMain`)
- Расположен в `nativeMain/kotlin/ru/ifmo/client`.
- Использует libcurl для выполнения HTTP-запросов.
- Основные файлы:
  - `HttpClient.native.kt`: Ожидаемая реализация HTTP-клиента для Native.
  - `CurlHttpClient.kt`: Обёртка над libcurl.

---

## Тесты
- Расположены в `commonTest/kotlin/ru/ifmo/client`.
- Основной файл: `HttpClientTest.kt`.
- Тесты покрывают все основные HTTP-методы: GET, POST, PUT, DELETE.
- Пример теста:
```kotlin
@Test
fun testGET() = runTest {
    HttpClient().use { client ->
        val response = client.request(HttpMethod.GET, HttpRequest("$JSON_PLACEHOLDER_HOST/posts/1"))
        assertEquals(HttpStatus(200), response.status)
    }
}
```

---

## Пример использования
### Отправка GET-запроса
```kotlin
HttpClient().use { client ->
    val response = client.request(HttpMethod.GET, HttpRequest("https://jsonplaceholder.typicode.com/posts/1"))
    println(response.body?.decodeToString())
}
```

### Отправка POST-запроса
```kotlin
HttpClient().use { client ->
    val response = client.request(
        HttpMethod.POST,
        HttpRequest(
            url = "https://jsonplaceholder.typicode.com/posts",
            headers = HttpHeaders(mapOf("Content-Type" to "application/json")),
            body = """{"title": "foo", "body": "bar", "userId": 1}""".encodeToByteArray()
        )
    )
    println(response.body?.decodeToString())
}
```

---

## Зависимости
Для работы модуля `nativeMain` необходимо установить libcurl. Инструкции:
- Windows: библиотека уже скомпилирована и доступна.
- macOS: `brew install curl`.
- Linux: `apt-get install libcurl4-gnutls-dev`.
