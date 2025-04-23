package chatbot.dsl.builder

import chatbot.api.ChatContext
import chatbot.dsl.ChatMarker
import chatbot.dsl.MessageProcessor
import chatbot.dsl.handler.MessageHandler

typealias HandlerType = MessageHandler<ChatContext?>

@ChatMarker
class IntoBuilder<C : ChatContext?>(
    val exactContext: C? = null,
) {
    val messageHandlers: MutableList<MessageHandler<*>> = mutableListOf()

    fun build(): MutableList<MessageHandler<ChatContext?>> {
        return messageHandlers.filterIsInstance<HandlerType>().toMutableList()
    }

    inline fun <reified T : ChatContext?> (IntoBuilder<T>).onCommand(
        command: String,
        noinline init: MessageProcessor<T>,
    ) {
        val handler =
            MessageHandler(
                messagePredicate = { it.text.startsWith("/") && it.text.drop(1).startsWith(command) },
                processor = init,
                contextPredicate = { exactContext != null && it == exactContext || it is T },
            )

        messageHandlers.add(handler)
    }

    inline fun <reified T : ChatContext?> (IntoBuilder<T>).onMessage(
        noinline messageProcessor: @ChatMarker MessageProcessor<T>,
    ) {
        val handler =
            MessageHandler(
                messagePredicate = { true },
                processor = messageProcessor,
                contextPredicate = { it is T },
            )

        messageHandlers.add(handler)
    }
}
