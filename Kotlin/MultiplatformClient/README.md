# Multiplatform HTTP Client

## Описание
Проект представляет собой мультиплатформенную обёртку для работы с HTTP-запросами. Цель — предоставить единое API для взаимодействия с HTTP-клиентами на различных платформах: JVM, JS (NodeJS, Browser) и Native.

---

## Поддерживаемые платформы и клиенты
1. **JVM**: Используется стандартный [Java HTTP Client](https://openjdk.org/groups/net/httpclient/intro.html).
2. **Native**: Обёртка над библиотекой [libcurl](https://curl.se/libcurl/).
3. **Browser JS**: Реализация на основе [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).
4. **Node JS**: Реализация на основе [node-fetch](https://www.npmjs.com/package/node-fetch).

---

## Структура проекта

### Общий модуль (`commonMain`)
- Содержит интерфейс `HttpClient` и общие модели:
  - `HttpRequest`
  - `HttpResponse`
  - `HttpHeaders`
  - `HttpMethod`
  - `HttpStatus`
- Основной сценарий использования описан в `Main.kt`.

### Платформенные модули
- `jvmMain`: Реализация для JVM с использованием Java HTTP Client.
- `jsMain`: Реализация для браузеров (Fetch API) и Node.js (node-fetch).
- `nativeMain`: Реализация для Native с использованием libcurl.

### Тесты
- `commonTest`: Содержит тесты для проверки корректности работы клиента на всех платформах.

---

## Установка и запуск

### Зависимости
Для работы проекта необходимо установить следующие зависимости:
1. **libcurl** (для Native):
   - Windows: библиотека уже скомпилирована и доступна.
   - macOS: `brew install curl`.
   - Linux: `apt-get install libcurl4-gnutls-dev` (или аналог для вашего дистрибутива).
2. **Chrome Browser** (для запуска тестов).

### Сборка и запуск
1. Установите [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) и [Gradle](https://gradle.org/).
2. Склонируйте репозиторий:
   ```bash
   git clone <repository_url>
   ```
3. Перейдите в каталог проекта:
   ```bash
   cd MultiplatformClient
   ```
4. Выполните сборку проекта:
   ```bash
   ./gradlew build
   ```
5. Запустите тесты:
   ```bash
   ./gradlew test
   ```

### Запуск сценария
Для запуска основного сценария выполните следующую команду на соответствующей платформе:
```bash
./gradlew run
```

---

## Модули и API
Для получения подробной информации о структуре проекта и API посетите [README в каталоге src](src/README.md).
