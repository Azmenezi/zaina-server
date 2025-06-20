package com.zaina.zaina.service

import com.zaina.zaina.dto.ProfileDto
import com.zaina.zaina.dto.UpdateProfileRequest
import com.zaina.zaina.dto.UserDto
import com.zaina.zaina.dto.UserSummaryDto
import com.zaina.zaina.entity.Profile
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.mapper.UserMapper
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.repository.UserRepository
import com.zaina.zaina.security.UserPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {

    fun getCurrentUser(): UserDto {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val user = userRepository.findById(userPrincipal.id)
            .orElseThrow { RuntimeException("User not found") }
        val profile = profileRepository.findByUserId(user.id)
        return UserMapper.toUserDto(user, profile)
    }

    fun getUserById(userId: UUID): UserDto {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        val profile = profileRepository.findByUserId(userId)
        return UserMapper.toUserDto(user, profile)
    }

    fun getAllUsers(): List<UserSummaryDto> {
        return userRepository.findAll().map { user ->
            val profile = profileRepository.findByUserId(user.id)
            UserMapper.toUserSummaryDto(user, profile)
        }
    }

    fun getUsersByRole(role: UserRole): List<UserSummaryDto> {
        return userRepository.findByRole(role).map { user ->
            val profile = profileRepository.findByUserId(user.id)
            UserMapper.toUserSummaryDto(user, profile)
        }
    }

    fun getUsersByCohort(cohortId: UUID): List<UserSummaryDto> {
        return userRepository.findByCohortId(cohortId).map { user ->
            val profile = profileRepository.findByUserId(user.id)
            UserMapper.toUserSummaryDto(user, profile)
        }
    }

    fun getProfileByUserId(userId: UUID): ProfileDto {
        val profile = profileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")
        return UserMapper.toProfileDto(profile)
    }

    fun updateProfile(userId: UUID, updateRequest: UpdateProfileRequest): ProfileDto {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        
        // Users can only update their own profile (participants, alumni, mentors can update)
        if (userPrincipal.id != userId) {
            throw RuntimeException("Access denied: You can only update your own profile")
        }

        // Check if user has permission to update profile (participants, alumni, mentors)
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        
        if (user.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Applicants cannot update profiles")
        }

        val existingProfile = profileRepository.findByUserId(userId)
            ?: throw RuntimeException("Profile not found")

        val updatedProfile = Profile(
            userId = existingProfile.userId,
            name = updateRequest.name,
            position = updateRequest.position,
            company = updateRequest.company,
            skills = updateRequest.skills,
            bio = updateRequest.bio,
            imageUrl = updateRequest.imageUrl,
            linkedinUrl = updateRequest.linkedinUrl
        )

        val savedProfile = profileRepository.save(updatedProfile)
        return UserMapper.toProfileDto(savedProfile)
    }

    fun searchProfiles(query: String): List<ProfileDto> {
        val profiles = profileRepository.searchProfiles(query)
        return profiles.map { UserMapper.toProfileDto(it) }
    }
} 