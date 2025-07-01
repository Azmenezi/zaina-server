package com.zaina.zaina.controller

import com.zaina.zaina.dto.*
import com.zaina.zaina.service.ConnectionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/connections")
@CrossOrigin(origins = ["*"], maxAge = 3600)
@PreAuthorize("hasRole('PARTICIPANT') or hasRole('ALUMNA') or hasRole('MENTOR')")
class ConnectionController(
    private val connectionService: ConnectionService
) {

    @PostMapping
    fun createConnection(@Valid @RequestBody request: CreateConnectionRequest): ResponseEntity<ConnectionResponse> {
        return try {
            val connection = connectionService.createConnection(request)
            ResponseEntity.ok(connection)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/pending")
    fun getPendingConnections(): ResponseEntity<List<ConnectionResponse>> {
        return try {
            val connections = connectionService.getPendingConnections()
            ResponseEntity.ok(connections)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{connectionId}")
    fun updateConnection(
        @PathVariable connectionId: UUID,
        @Valid @RequestBody request: UpdateConnectionRequest
    ): ResponseEntity<ConnectionResponse> {
        return try {
            val connection = connectionService.updateConnection(connectionId, request)
            ResponseEntity.ok(connection)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/accepted")
    fun getAcceptedConnections(): ResponseEntity<List<ConnectionResponse>> {
        return try {
            val connections = connectionService.getAcceptedConnections()
            ResponseEntity.ok(connections)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 