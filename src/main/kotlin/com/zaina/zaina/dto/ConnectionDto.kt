package com.zaina.zaina.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.zaina.zaina.entity.ConnectionStatus
import com.zaina.zaina.entity.ConnectionType
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class CreateConnectionRequest(
    @field:NotNull(message = "Target ID is required")
    val targetId: UUID,
    
    @field:NotNull(message = "Connection type is required")
    val type: ConnectionType
)

data class UpdateConnectionRequest(
    @field:NotNull(message = "Status is required")
    val status: ConnectionStatus
)

data class ConnectionResponse(
    val id: UUID,
    val requesterId: UUID,
    val targetId: UUID,
    val type: ConnectionType,
    val status: ConnectionStatus,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val requestedAt: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val respondedAt: LocalDateTime?,
    val requesterName: String?,
    val targetName: String?
) 