package chatbot.dsl.builder

import chatbot.api.Keyboard

class KeyboardBuilder {
    // Если true, клавиатура скрывается после первого нажатия на любую кнопку
    var oneTime: Boolean = false
    var keyboard = mutableListOf<MutableList<Keyboard.Button>>()

    fun row(init: RowBuilder.() -> Unit) {
        val rowBuilder = RowBuilder()
        rowBuilder.init()
        keyboard.add(rowBuilder.buttons)
    }

    fun build(): Keyboard.Markup {
        return Keyboard.Markup(oneTime, keyboard)
    }
}
