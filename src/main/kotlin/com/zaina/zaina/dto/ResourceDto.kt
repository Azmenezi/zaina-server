package com.zaina.zaina.dto

import com.zaina.zaina.entity.ResourceType
import com.zaina.zaina.entity.UserRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class ResourceDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val type: ResourceType,
    val url: String,
    val targetRoles: List<UserRole>,
    val module: String?
)

data class CreateResourceRequest(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    @field:NotNull
    val type: ResourceType,
    @field:NotBlank
    val url: String,
    val targetRoles: List<UserRole> = listOf(),
    val module: String? = null
)

data class UpdateResourceRequest(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    @field:NotNull
    val type: ResourceType,
    @field:NotBlank
    val url: String,
    val targetRoles: List<UserRole> = listOf(),
    val module: String? = null
) 