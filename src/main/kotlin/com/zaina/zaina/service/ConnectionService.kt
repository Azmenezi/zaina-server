package com.zaina.zaina.service

import com.zaina.zaina.dto.*
import com.zaina.zaina.entity.*
import com.zaina.zaina.mapper.ConnectionMapper
import com.zaina.zaina.repository.ConnectionRepository
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.repository.UserRepository
import com.zaina.zaina.security.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class ConnectionService(
    private val connectionRepository: ConnectionRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {

    fun createConnection(request: CreateConnectionRequest): ConnectionResponse {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val requesterId = userPrincipal.id

        // Verify requester exists
        val requester = userRepository.findById(requesterId)
            .orElseThrow { RuntimeException("Requester not found") }

        // Verify target user exists
        val target = userRepository.findById(request.targetId)
            .orElseThrow { RuntimeException("Target user not found") }

        // Check if requesting self
        if (requesterId == request.targetId) {
            throw RuntimeException("Cannot create connection with yourself")
        }

        // Validate connection type permissions
        if (request.type == ConnectionType.MENTORSHIP) {
            // Only mentors can receive mentorship requests
            if (target.role != UserRole.MENTOR) {
                throw RuntimeException("Mentorship requests can only be sent to mentors")
            }
        }

        // Check if connection already exists
        if (connectionRepository.existsByRequesterIdAndTargetIdAndType(requesterId, request.targetId, request.type)) {
            throw RuntimeException("Connection request already exists")
        }

        val connection = Connection(
            requesterId = requesterId,
            targetId = request.targetId,
            type = request.type,
            status = ConnectionStatus.PENDING,
            requestedAt = LocalDateTime.now()
        )

        val savedConnection = connectionRepository.save(connection)
        return ConnectionMapper.toConnectionResponse(savedConnection, requester, target)
    }

    fun getPendingConnections(): List<ConnectionResponse> {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val userId = userPrincipal.id

        val pendingConnections = connectionRepository.findByTargetIdAndStatus(userId, ConnectionStatus.PENDING)
        
        return pendingConnections.map { connection ->
            val requester = userRepository.findById(connection.requesterId)
                .orElseThrow { RuntimeException("Requester not found") }
            val target = userRepository.findById(connection.targetId)
                .orElseThrow { RuntimeException("Target not found") }
            ConnectionMapper.toConnectionResponse(connection, requester, target)
        }
    }

    fun updateConnection(connectionId: UUID, request: UpdateConnectionRequest): ConnectionResponse {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val userId = userPrincipal.id

        val connection = connectionRepository.findById(connectionId)
            .orElseThrow { RuntimeException("Connection not found") }

        // Only the target user can update the connection status
        if (connection.targetId != userId) {
            throw RuntimeException("Access denied: Only the target user can update connection status")
        }

        // Can only update pending connections
        if (connection.status != ConnectionStatus.PENDING) {
            throw RuntimeException("Connection has already been responded to")
        }

        val updatedConnection = connection.copy(
            status = request.status,
            respondedAt = LocalDateTime.now()
        )

        val savedConnection = connectionRepository.save(updatedConnection)
        
        val requester = userRepository.findById(connection.requesterId)
            .orElseThrow { RuntimeException("Requester not found") }
        val target = userRepository.findById(connection.targetId)
            .orElseThrow { RuntimeException("Target not found") }
            
        return ConnectionMapper.toConnectionResponse(savedConnection, requester, target)
    }

    fun getAcceptedConnections(): List<ConnectionResponse> {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val userId = userPrincipal.id

        val acceptedConnections = connectionRepository.findConnectionsByUserAndStatus(userId, ConnectionStatus.ACCEPTED)
        
        return acceptedConnections.map { connection ->
            val requester = userRepository.findById(connection.requesterId)
                .orElseThrow { RuntimeException("Requester not found") }
            val target = userRepository.findById(connection.targetId)
                .orElseThrow { RuntimeException("Target not found") }
            ConnectionMapper.toConnectionResponse(connection, requester, target)
        }
    }
} 