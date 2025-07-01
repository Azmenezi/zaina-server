package com.zaina.zaina.repository

import com.zaina.zaina.entity.Connection
import com.zaina.zaina.entity.ConnectionStatus
import com.zaina.zaina.entity.ConnectionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ConnectionRepository : JpaRepository<Connection, UUID> {
    
    fun findByTargetIdAndStatus(targetId: UUID, status: ConnectionStatus): List<Connection>
    
    fun findByRequesterIdAndTargetIdAndType(
        requesterId: UUID, 
        targetId: UUID, 
        type: ConnectionType
    ): Connection?
    
    @Query("""
        SELECT c FROM Connection c 
        WHERE (c.requesterId = :userId OR c.targetId = :userId) 
        AND c.status = :status
    """)
    fun findConnectionsByUserAndStatus(
        @Param("userId") userId: UUID,
        @Param("status") status: ConnectionStatus
    ): List<Connection>
    
    @Query("""
        SELECT c FROM Connection c 
        WHERE ((c.requesterId = :userId1 AND c.targetId = :userId2) 
           OR (c.requesterId = :userId2 AND c.targetId = :userId1))
        AND c.type = :type
    """)
    fun findConnectionBetweenUsers(
        @Param("userId1") userId1: UUID,
        @Param("userId2") userId2: UUID,
        @Param("type") type: ConnectionType
    ): Connection?
    
    fun existsByRequesterIdAndTargetIdAndType(
        requesterId: UUID, 
        targetId: UUID, 
        type: ConnectionType
    ): Boolean
} 