package com.zaina.zaina.controller

import com.zaina.zaina.dto.UserStatus
import com.zaina.zaina.service.WebSocketService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/websocket")
@CrossOrigin(origins = ["*"], maxAge = 3600)
@PreAuthorize("hasRole('PARTICIPANT') or hasRole('ALUMNA') or hasRole('MENTOR')")
class WebSocketController(
    private val webSocketService: WebSocketService
) {

    @GetMapping("/online-users")
    fun getOnlineUsers(): ResponseEntity<List<UserStatus>> {
        return try {
            val onlineUsers = webSocketService.getOnlineUsers()
            val userStatusList = onlineUsers.map { (userId, lastSeen) ->
                UserStatus(
                    userId = userId,
                    online = true,
                    lastSeen = lastSeen
                )
            }
            ResponseEntity.ok(userStatusList)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/user-status/{userId}")
    fun getUserStatus(@PathVariable userId: UUID): ResponseEntity<UserStatus> {
        return try {
            val isOnline = webSocketService.isUserOnline(userId)
            val lastSeen = webSocketService.getUserLastSeen(userId)
            
            val userStatus = UserStatus(
                userId = userId,
                online = isOnline,
                lastSeen = lastSeen
            )
            
            ResponseEntity.ok(userStatus)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 