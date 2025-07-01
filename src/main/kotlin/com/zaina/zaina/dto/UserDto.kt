package com.zaina.zaina.dto

import com.zaina.zaina.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.*

data class UserDto(
    val id: UUID,
    val email: String,
    val role: UserRole,
    val cohortId: UUID?,
    val profile: ProfileDto?,
    // Additional user information
    val createdAt: LocalDateTime? = null,
    val lastLoginAt: LocalDateTime? = null,
    val isActive: Boolean = true,
    val accountStatus: String = "ACTIVE"
)

data class ProfileDto(
    val userId: UUID,
    val name: String,
    val position: String?,
    val company: String?,
    val skills: List<String>,
    val bio: String?,
    val imageUrl: String?,
    val linkedinUrl: String?
)

data class UpdateProfileRequest(
    @field:NotBlank
    val name: String,
    val position: String? = null,
    val company: String? = null,
    val skills: List<String> = listOf(),
    val bio: String? = null,
    val imageUrl: String? = null,
    val linkedinUrl: String? = null
)

data class UserSummaryDto(
    val id: UUID,
    val email: String,
    val role: UserRole,
    val name: String?,
    val position: String?,
    val company: String?
) 