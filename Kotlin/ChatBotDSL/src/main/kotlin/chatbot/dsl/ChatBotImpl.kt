package chatbot.dsl

import chatbot.api.ChatBot
import chatbot.api.ChatContextsManager
import chatbot.api.Client
import chatbot.api.LogLevel
import chatbot.api.Message

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class ChatMarker

@ChatMarker
class ChatBotImpl(
    private val client: Client,
    private var contextManager: ChatContextsManager? = null,
    override var logLevel: LogLevel = LogLevel.ERROR,
) : ChatBot {
    private val behaviours = mutableListOf<Behaviour>()

    override fun processMessages(message: Message) {
        val chatId = message.chatId
        val currentContext = contextManager?.getContext(chatId)

        for (behavior in behaviours) {
            if (behavior.appliesTo(message, currentContext)) {
                behavior.process(message, client, currentContext, contextManager)
                return
            }
        }
    }

    fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    operator fun LogLevel.unaryPlus() {
        logLevel = this
    }

    fun use(context: ChatContextsManager) {
        this.contextManager = context
    }

    fun behaviour(init: Behaviour.() -> Unit) {
        val behaviour = Behaviour().apply(init)
        behaviours.add(behaviour)
    }
}

fun chatBot(
    client: Client,
    init: ChatBotImpl.() -> Unit,
): ChatBotImpl {
    val chatBot = ChatBotImpl(client)
    return chatBot.apply(init)
}
