package com.zaina.zaina.mapper

import com.zaina.zaina.dto.*
import com.zaina.zaina.entity.Profile
import com.zaina.zaina.entity.User

object UserMapper {
    
    fun toUserDto(user: User, profile: Profile? = null): UserDto {
        return UserDto(
            id = user.id,
            email = user.email,
            role = user.role,
            cohortId = user.cohortId,
            profile = profile?.let { toProfileDto(it) }
        )
    }
    
    fun toProfileDto(profile: Profile): ProfileDto {
        return ProfileDto(
            userId = profile.userId,
            name = profile.name,
            position = profile.position,
            company = profile.company,
            skills = profile.skills,
            bio = profile.bio,
            imageUrl = profile.imageUrl,
            linkedinUrl = profile.linkedinUrl
        )
    }
    
    fun toUserSummaryDto(user: User, profile: Profile? = null): UserSummaryDto {
        return UserSummaryDto(
            id = user.id,
            email = user.email,
            role = user.role,
            name = profile?.name,
            position = profile?.position,
            company = profile?.company
        )
    }
} 