package com.xiaoxisi.data.repository

import com.xiaoxisi.data.local.dao.MessageDao
import com.xiaoxisi.data.local.entity.MessageEntity
import com.xiaoxisi.domain.model.Message
import com.xiaoxisi.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getAllMessages(): Flow<List<Message>> {
        return messageDao.getAllMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getRecentMessages(limit: Int = 20): List<Message> {
        return messageDao.getRecentMessages(limit)
            .reversed()
            .map { it.toDomain() }
    }

    suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
    }

    suspend fun clearAll() {
        messageDao.clearAll()
    }

    private fun MessageEntity.toDomain(): Message {
        return Message(
            id = id,
            role = MessageRole.valueOf(role),
            content = content,
            timestamp = timestamp,
            intent = intent,
            entities = emptyMap()
        )
    }

    private fun Message.toEntity(): MessageEntity {
        return MessageEntity(
            id = id,
            role = role.name,
            content = content,
            timestamp = timestamp,
            intent = intent,
            entities = entities.entries.joinToString(",") { "${it.key}=${it.value}" }
        )
    }
}
