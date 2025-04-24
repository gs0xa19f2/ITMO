package chatbot.dsl.handler

import chatbot.api.ChatContext
import chatbot.api.ChatContextsManager
import chatbot.api.Client
import chatbot.api.Message
import chatbot.dsl.MessageProcessor
import chatbot.dsl.MessageProcessorContext

class MessageHandler<C : ChatContext?>(
    val messagePredicate: (Message) -> Boolean,
    val processor: MessageProcessor<C>,
    val contextPredicate: ((ChatContext?) -> Boolean)? = null,
) {
    fun appliesTo(
        other: Message,
        context: ChatContext?,
    ): Boolean {
        return messagePredicate(other) && (contextPredicate?.invoke(context) ?: true)
    }

    fun process(
        message: Message,
        client: Client,
        context: C,
        contextManager: ChatContextsManager?,
    ) {
        if (appliesTo(message, context)) {
            processor.invoke(
                MessageProcessorContext(message, client, context) { newContext ->
                    contextManager?.setContext(message.chatId, newContext)
                },
            )
        }
    }
}
