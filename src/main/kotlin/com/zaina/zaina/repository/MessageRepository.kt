package com.zaina.zaina.repository

import com.zaina.zaina.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {
    
    @Query("""
        SELECT m FROM Message m 
        WHERE (m.senderId = :userId1 AND m.receiverId = :userId2) 
           OR (m.senderId = :userId2 AND m.receiverId = :userId1)
        ORDER BY m.sentAt ASC
    """)
    fun findConversationBetweenUsers(
        @Param("userId1") userId1: UUID,
        @Param("userId2") userId2: UUID
    ): List<Message>
    
    fun findBySenderIdOrReceiverIdOrderBySentAtDesc(senderId: UUID, receiverId: UUID): List<Message>
    
    fun findByIdAndReceiverId(messageId: UUID, receiverId: UUID): Message?
} 