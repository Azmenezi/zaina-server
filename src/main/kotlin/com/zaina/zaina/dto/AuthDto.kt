package com.zaina.zaina.dto

import com.zaina.zaina.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class LoginRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    
    @field:NotBlank
    @field:Size(min = 6, max = 100)
    val password: String
)

data class RegisterRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    
    @field:NotBlank
    @field:Size(min = 6, max = 100)
    val password: String,
    
    @field:NotBlank
    val name: String,
    
    val position: String? = null,
    val company: String? = null,
    val bio: String? = null,
    val cohortId: UUID? = null
)

data class JwtResponse(
    val token: String,
    val type: String = "Bearer",
    val id: UUID,
    val email: String,
    val role: UserRole,
    val name: String?
) 