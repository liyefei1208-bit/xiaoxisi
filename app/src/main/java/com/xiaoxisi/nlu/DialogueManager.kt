package com.xiaoxisi.nlu

import com.xiaoxisi.domain.model.ConversationContext
import com.xiaoxisi.domain.model.Message
import com.xiaoxisi.domain.model.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogueManager @Inject constructor() {

    private val context = ConversationContext()

    fun addMessage(message: Message) {
        val messages = context.messages.toMutableList()
        messages.add(message)

        if (messages.size > 50) {
            messages.removeAt(0)
        }
    }

    fun getContext(): ConversationContext = context

    fun getLastIntent(): String? = context.lastIntent

    fun clear() {
    }
}
