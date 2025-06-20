package com.zaina.zaina.mapper

import com.zaina.zaina.dto.ResourceDto
import com.zaina.zaina.entity.Resource

object ResourceMapper {
    
    fun toResourceDto(resource: Resource): ResourceDto {
        return ResourceDto(
            id = resource.id,
            title = resource.title,
            description = resource.description,
            type = resource.type,
            url = resource.url,
            targetRoles = resource.targetRoles,
            module = resource.module
        )
    }
} 