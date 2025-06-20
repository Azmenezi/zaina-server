package com.zaina.zaina.mapper

import com.zaina.zaina.dto.ConnectionResponse
import com.zaina.zaina.entity.Connection
import com.zaina.zaina.entity.User
import com.zaina.zaina.repository.ProfileRepository

object ConnectionMapper {
    
    fun toConnectionResponse(
        connection: Connection, 
        requester: User, 
        target: User,
        profileRepository: ProfileRepository? = null
    ): ConnectionResponse {
        
        // Get names from profile repository if available
        val requesterName = profileRepository?.findByUserId(requester.id)?.name
        val targetName = profileRepository?.findByUserId(target.id)?.name
        
        return ConnectionResponse(
            id = connection.id,
            requesterId = connection.requesterId,
            targetId = connection.targetId,
            type = connection.type,
            status = connection.status,
            requestedAt = connection.requestedAt,
            respondedAt = connection.respondedAt,
            requesterName = requesterName,
            targetName = targetName
        )
    }
    
    // Simplified version without name lookup
    fun toConnectionResponse(
        connection: Connection, 
        requester: User, 
        target: User
    ): ConnectionResponse {
        return ConnectionResponse(
            id = connection.id,
            requesterId = connection.requesterId,
            targetId = connection.targetId,
            type = connection.type,
            status = connection.status,
            requestedAt = connection.requestedAt,
            respondedAt = connection.respondedAt,
            requesterName = null,
            targetName = null
        )
    }
} 