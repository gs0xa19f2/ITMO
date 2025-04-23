package chatbot.dsl.builder

import chatbot.api.ChatContext
import chatbot.api.ChatId
import chatbot.api.Client
import chatbot.api.Keyboard
import chatbot.api.MessageId
import chatbot.dsl.MessageProcessorContext

class MessageBuilder(private val client: Client, private val chatId: ChatId) {
    var text: String = ""
    var replyTo: MessageId? = null
    private var keyboard: Keyboard? = null

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
    }

    fun withKeyboard(init: KeyboardBuilder.() -> Unit) {
        val builder = KeyboardBuilder()
        builder.init()
        keyboard = builder.build()
    }

    fun send() {
        val keyboardContent = (keyboard as? Keyboard.Markup)?.keyboard

        if (text.isNotEmpty() || keyboard != null &&
            keyboardContent?.any { it.isNotEmpty() } != false
        ) {
            client.sendMessage(chatId, text, keyboard, replyTo)
        }
    }
}

fun MessageProcessorContext<ChatContext?>.sendMessage(
    chatId: ChatId,
    init: MessageBuilder.() -> Unit,
) {
    val builder = MessageBuilder(this.client, chatId).apply(init)
    builder.send()
}
