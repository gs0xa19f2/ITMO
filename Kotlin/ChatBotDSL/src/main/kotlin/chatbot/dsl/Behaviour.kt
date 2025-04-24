package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.ChatContextsManager
import chatbot.api.Client
import chatbot.api.Message
import chatbot.dsl.builder.IntoBuilder
import chatbot.dsl.handler.MessageHandler

@ChatMarker
class Behaviour {
    val messageHandlers = mutableListOf<MessageHandler<ChatContext?>>()

    fun onCommand(
        command: String,
        init: @ChatMarker MessageProcessor<ChatContext?>,
    ) = onMessage({ it.text.startsWith("/") && it.text.drop(1).startsWith(command) }, init)

    fun onMessagePrefix(
        prefix: String,
        init: @ChatMarker MessageProcessor<ChatContext?>,
    ) = onMessage({ it.text.startsWith(prefix) }, init)

    fun onMessageContains(
        text: String,
        init: @ChatMarker MessageProcessor<ChatContext?>,
    ) = onMessage({ it.text.contains(text) }, init)

    fun onMessage(
        messageTextExactly: String,
        init: @ChatMarker MessageProcessor<ChatContext?>,
    ) = onMessage({ it.text == messageTextExactly }, init)

    fun onMessage(init: @ChatMarker MessageProcessor<ChatContext?>) = onMessage({ true }, init)

    fun onMessage(
        predicate: (Message) -> Boolean,
        init: @ChatMarker MessageProcessor<ChatContext?>,
    ) = messageHandlers.add(MessageHandler(predicate, init))

    inline fun <reified T : ChatContext?> into(init: IntoBuilder<T>.() -> Unit) {
        val builder = IntoBuilder<T>()
        builder.init()

        for (entity in builder.build()) {
            messageHandlers.add(entity)
        }
    }

    inline infix fun <T : ChatContext?> T.into(init: IntoBuilder<T>.() -> Unit) {
        val builder = IntoBuilder(this)
        builder.init()

        for (entity in builder.build()) {
            messageHandlers.add(entity)
        }
    }

    fun appliesTo(
        message: Message,
        context: ChatContext?,
    ): Boolean = messageHandlers.any { it.appliesTo(message, context) }

    fun process(
        message: Message,
        client: Client,
        context: ChatContext?,
        contextManager: ChatContextsManager?,
    ) {
        for (handler in messageHandlers) {
            if (handler.appliesTo(message, context)) {
                handler.process(
                    message,
                    client,
                    context,
                    contextManager,
                )

                return
            }
        }
    }
}
