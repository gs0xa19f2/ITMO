# ChatBotDSL

## Описание
Проект представляет собой DSL (Domain-Specific Language) для декларативного описания функциональности чат-ботов. Он предоставляет простой и удобный способ настройки поведения бота, управления контекстами чатов и обработки сообщений.

---

## Основные возможности

### 1. Настройка уровня логирования
Поддерживает настройку уровня логирования через:
- Конструкцию `use`:
```kotlin
chatBot(client) {
    use(LogLevel.INFO)
}
```
- Унарный оператор `+`:
```kotlin
chatBot(client) {
    +LogLevel.INFO
}
```

---

### 2. Поведение бота
Модуль `behaviour` позволяет определить обработчики событий:
- `onCommand(command: String)` — обработка команд.
- `onMessage(predicate: ChatBot.(Message) -> Boolean)` — обработка сообщений, удовлетворяющих предикату.
- Дополнительные обработчики, такие как `onMessagePrefix`, `onMessageContains`, `onMessage(messageTextExactly)`, `onMessage`.

Пример:
```kotlin
chatBot(client) {
    behaviour {
        onCommand("help") {
            client.sendMessage(message.chatId, "How can I help you?")
        }

        onMessage("ping") {
            client.sendMessage(message.chatId, "pong", replyMessageId = message.id)
        }
    }
}
```

---

### 3. Контексты чатов
Поддерживается хранение и управление контекстами для каждого чата:
- Подключение менеджеров контекстов через `use` или оператор `=`:
```kotlin
chatBot {
    use(contextManager)
    // или
    contextManager = someContextManager
}
```
- Обработчики контекстов:
```kotlin
AskNameContext.into {
    onMessage {
        client.sendMessage(message.chatId, "What is your name?")
        setContext(WithNameContext(message.text))
    }
}
```

---

### 4. Билдер сообщений
DSL для отправки сообщений с поддержкой кнопок:
- Настройка текста, клавиатуры и кнопок:
```kotlin
sendMessage(chatId) {
    text = "How can I help you?"
    withKeyboard {
        oneTime = true
        row {
            button("Option 1")
            button("Option 2")
        }
    }
}
```
- Удаление клавиатуры:
```kotlin
sendMessage(chatId) {
    removeKeyboard()
}
```

---

### 5. Тестирование
Проект содержит модульные тесты (`src/test/kotlin`) для проверки функциональности DSL, включая обработку команд, управление контекстами и отправку сообщений.

---

## Дополнительная информация
Для подробного описания API и DSL посетите [документацию в src/main/kotlin/chatbot](src/main/kotlin/chatbot/README.md).
