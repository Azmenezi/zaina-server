package com.zaina.zaina.controller

import com.zaina.zaina.dto.JwtResponse
import com.zaina.zaina.dto.LoginRequest
import com.zaina.zaina.dto.RegisterRequest
import com.zaina.zaina.service.AuthService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class AuthController(
    private val authService: AuthService
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<JwtResponse> {
        return try {
            logger.info("Registration attempt for email: ${registerRequest.email}")
            val response = authService.registerUser(registerRequest)
            logger.info("Registration successful for email: ${registerRequest.email}")
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("Registration failed for email: ${registerRequest.email} - Error: ${e.message}", e)
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Unexpected error during registration for email: ${registerRequest.email} - Error: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<JwtResponse> {
        return try {
            logger.info("Login attempt for email: ${loginRequest.email}")
            val response = authService.authenticateUser(loginRequest)
            logger.info("Login successful for email: ${loginRequest.email}")
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            logger.error("Login failed for email: ${loginRequest.email} - Error: ${e.message}", e)
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Unexpected error during login for email: ${loginRequest.email} - Error: ${e.message}", e)
            ResponseEntity.internalServerError().build()
        }
    }
} 