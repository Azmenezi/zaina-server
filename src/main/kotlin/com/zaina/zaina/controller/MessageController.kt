package com.zaina.zaina.controller

import com.zaina.zaina.dto.*
import com.zaina.zaina.service.MessageService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = ["*"], maxAge = 3600)
@PreAuthorize("hasRole('USER')")
class MessageController(
    private val messageService: MessageService
) {

    @PostMapping
    fun sendMessage(@Valid @RequestBody request: SendMessageRequest): ResponseEntity<MessageResponse> {
        return try {
            val message = messageService.sendMessage(request)
            ResponseEntity.ok(message)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/thread/{otherUserId}")
    fun getConversation(@PathVariable otherUserId: UUID): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = messageService.getConversation(otherUserId)
            ResponseEntity.ok(conversation)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{messageId}/read")
    fun markMessageAsRead(@PathVariable messageId: UUID): ResponseEntity<MessageResponse> {
        return try {
            val message = messageService.markMessageAsRead(messageId)
            ResponseEntity.ok(message)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 