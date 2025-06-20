package com.zaina.zaina.service

import com.zaina.zaina.dto.CohortDto
import com.zaina.zaina.dto.CreateCohortRequest
import com.zaina.zaina.dto.UpdateCohortRequest
import com.zaina.zaina.dto.UserSummaryDto
import com.zaina.zaina.entity.Cohort
import com.zaina.zaina.entity.User
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.mapper.CohortMapper
import com.zaina.zaina.mapper.UserMapper
import com.zaina.zaina.repository.CohortRepository
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.repository.UserRepository
import com.zaina.zaina.security.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class CohortService(
    private val cohortRepository: CohortRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {

    fun getAllCohorts(): List<CohortDto> {
        return cohortRepository.findAll().map { cohort ->
            val participantCount = userRepository.findByCohortId(cohort.id).size
            CohortMapper.toCohortDto(cohort, participantCount)
        }
    }

    fun getCohortById(cohortId: UUID): CohortDto {
        val cohort = cohortRepository.findById(cohortId)
            .orElseThrow { RuntimeException("Cohort not found") }
        
        val participantCount = userRepository.findByCohortId(cohort.id).size
        return CohortMapper.toCohortDto(cohort, participantCount)
    }

    fun getActiveCohorts(): List<CohortDto> {
        return cohortRepository.findActiveCohorts().map { cohort ->
            val participantCount = userRepository.findByCohortId(cohort.id).size
            CohortMapper.toCohortDto(cohort, participantCount)
        }
    }

    fun getUpcomingCohorts(): List<CohortDto> {
        return cohortRepository.findUpcomingCohorts().map { cohort ->
            val participantCount = userRepository.findByCohortId(cohort.id).size
            CohortMapper.toCohortDto(cohort, participantCount)
        }
    }

    fun getCompletedCohorts(): List<CohortDto> {
        return cohortRepository.findCompletedCohorts().map { cohort ->
            val participantCount = userRepository.findByCohortId(cohort.id).size
            CohortMapper.toCohortDto(cohort, participantCount)
        }
    }

    fun createCohort(createRequest: CreateCohortRequest): CohortDto {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants and alumni can create cohorts (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants and alumni can create cohorts")
        }

        // Check if cohort with same name already exists
        if (cohortRepository.findByName(createRequest.name) != null) {
            throw RuntimeException("Cohort with name '${createRequest.name}' already exists")
        }

        val cohort = Cohort(
            name = createRequest.name,
            startDate = createRequest.startDate,
            endDate = createRequest.endDate
        )

        val savedCohort = cohortRepository.save(cohort)
        return CohortMapper.toCohortDto(savedCohort)
    }

    fun updateCohort(cohortId: UUID, updateRequest: UpdateCohortRequest): CohortDto {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants and alumni can update cohorts (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants and alumni can update cohorts")
        }

        val existingCohort = cohortRepository.findById(cohortId)
            .orElseThrow { RuntimeException("Cohort not found") }

        // Check if cohort with same name already exists (excluding current cohort)
        val cohortWithSameName = cohortRepository.findByName(updateRequest.name)
        if (cohortWithSameName != null && cohortWithSameName.id != cohortId) {
            throw RuntimeException("Cohort with name '${updateRequest.name}' already exists")
        }

        val updatedCohort = Cohort(
            id = existingCohort.id,
            name = updateRequest.name,
            startDate = updateRequest.startDate,
            endDate = updateRequest.endDate
        )

        val savedCohort = cohortRepository.save(updatedCohort)
        val participantCount = userRepository.findByCohortId(savedCohort.id).size
        return CohortMapper.toCohortDto(savedCohort, participantCount)
    }

    fun deleteCohort(cohortId: UUID) {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants and alumni can delete cohorts (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants and alumni can delete cohorts")
        }

        // Check if cohort has participants
        val participants = userRepository.findByCohortId(cohortId)
        if (participants.isNotEmpty()) {
            throw RuntimeException("Cannot delete cohort: ${participants.size} participants are assigned to this cohort")
        }

        cohortRepository.deleteById(cohortId)
    }

    fun getCohortMembers(cohortId: UUID): List<UserSummaryDto> {
        // Verify cohort exists
        cohortRepository.findById(cohortId)
            .orElseThrow { RuntimeException("Cohort not found") }
        
        return userRepository.findByCohortId(cohortId).map { user ->
            val profile = profileRepository.findByUserId(user.id)
            UserMapper.toUserSummaryDto(user, profile)
        }
    }

    fun assignUserToCohort(cohortId: UUID, userId: UUID) {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants and alumni can assign users to cohorts
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants and alumni can assign users to cohorts")
        }

        // Verify cohort exists
        cohortRepository.findById(cohortId)
            .orElseThrow { RuntimeException("Cohort not found") }

        // Get the user to assign
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        // Update user's cohort
        val updatedUser = User(
            id = user.id,
            email = user.email,
            passwordHash = user.passwordHash,
            role = user.role,
            cohortId = cohortId
        )

        userRepository.save(updatedUser)
    }

    fun removeUserFromCohort(cohortId: UUID, userId: UUID) {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants and alumni can remove users from cohorts
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants and alumni can remove users from cohorts")
        }

        // Verify cohort exists
        cohortRepository.findById(cohortId)
            .orElseThrow { RuntimeException("Cohort not found") }

        // Get the user to remove
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        // Remove user from cohort
        val updatedUser = User(
            id = user.id,
            email = user.email,
            passwordHash = user.passwordHash,
            role = user.role,
            cohortId = null
        )

        userRepository.save(updatedUser)
    }

    private fun getCurrentUserPrincipal(): UserPrincipal {
        return SecurityContextHolder.getContext().authentication.principal as UserPrincipal
    }
} 