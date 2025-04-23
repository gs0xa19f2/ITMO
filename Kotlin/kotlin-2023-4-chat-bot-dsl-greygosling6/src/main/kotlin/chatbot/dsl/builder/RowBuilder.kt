package chatbot.dsl.builder

import chatbot.api.Keyboard

class RowBuilder {
    val buttons = mutableListOf<Keyboard.Button>()

    operator fun String.unaryMinus() {
        buttons.add(Keyboard.Button(text = this))
    }

    fun button(text: String) {
        buttons.add(Keyboard.Button(text))
    }
}
