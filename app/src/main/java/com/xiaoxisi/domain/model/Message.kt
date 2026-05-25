package com.xiaoxisi.domain.model

data class Message(
    val id: Long = 0,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val intent: String? = null,
    val entities: Map<String, String> = emptyMap()
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ConversationContext(
    val messages: List<Message> = emptyList(),
    val lastIntent: String? = null,
    val pendingAction: String? = null
)
