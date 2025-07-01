package com.zaina.zaina.controller

import com.zaina.zaina.dto.ProfileDto
import com.zaina.zaina.dto.UpdateProfileRequest
import com.zaina.zaina.dto.UserDto
import com.zaina.zaina.dto.UserSummaryDto
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.service.UserService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class UserController(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<Any> {
        return try {
            logger.info("Fetching current user data")
            val user = userService.getCurrentUser()
            logger.info("Successfully retrieved user data for user ID: ${user.id}")
            ResponseEntity.ok(user)
        } catch (e: RuntimeException) {
            logger.error("Error fetching current user: ${e.message}")
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Unexpected error fetching current user", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "An unexpected error occurred"))
        }
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: UUID): ResponseEntity<UserDto> {
        return try {
            val user = userService.getUserById(userId)
            ResponseEntity.ok(user)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserSummaryDto>> {
        return try {
            val users = userService.getAllUsers()
            ResponseEntity.ok(users)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/role/{role}")
    fun getUsersByRole(@PathVariable role: UserRole): ResponseEntity<List<UserSummaryDto>> {
        return try {
            val users = userService.getUsersByRole(role)
            ResponseEntity.ok(users)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/cohort/{cohortId}")
    fun getUsersByCohort(@PathVariable cohortId: UUID): ResponseEntity<List<UserSummaryDto>> {
        return try {
            val users = userService.getUsersByCohort(cohortId)
            ResponseEntity.ok(users)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
}

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class ProfileController(
    private val userService: UserService
) {

    @GetMapping("/{userId}")
    fun getProfileByUserId(@PathVariable userId: UUID): ResponseEntity<ProfileDto> {
        return try {
            val profile = userService.getProfileByUserId(userId)
            ResponseEntity.ok(profile)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{userId}")
    fun updateProfile(
        @PathVariable userId: UUID,
        @Valid @RequestBody updateRequest: UpdateProfileRequest
    ): ResponseEntity<ProfileDto> {
        return try {
            val profile = userService.updateProfile(userId, updateRequest)
            ResponseEntity.ok(profile)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/search")
    fun searchProfiles(@RequestParam query: String): ResponseEntity<List<ProfileDto>> {
        return try {
            val profiles = userService.searchProfiles(query)
            ResponseEntity.ok(profiles)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 