package com.zaina.zaina.controller

import com.zaina.zaina.dto.CohortDto
import com.zaina.zaina.dto.CreateCohortRequest
import com.zaina.zaina.dto.UpdateCohortRequest
import com.zaina.zaina.dto.UserSummaryDto
import com.zaina.zaina.service.CohortService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/cohorts")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class CohortController(
    private val cohortService: CohortService
) {

    @GetMapping
    fun getAllCohorts(): ResponseEntity<List<CohortDto>> {
        return try {
            val cohorts = cohortService.getAllCohorts()
            ResponseEntity.ok(cohorts)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{cohortId}")
    fun getCohortById(@PathVariable cohortId: UUID): ResponseEntity<CohortDto> {
        return try {
            val cohort = cohortService.getCohortById(cohortId)
            ResponseEntity.ok(cohort)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/active")
    fun getActiveCohorts(): ResponseEntity<List<CohortDto>> {
        return try {
            val cohorts = cohortService.getActiveCohorts()
            ResponseEntity.ok(cohorts)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/current")
    fun getCurrentCohorts(): ResponseEntity<List<CohortDto>> {
        return try {
            val cohorts = cohortService.getActiveCohorts()
            ResponseEntity.ok(cohorts)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/upcoming")
    fun getUpcomingCohorts(): ResponseEntity<List<CohortDto>> {
        return try {
            val cohorts = cohortService.getUpcomingCohorts()
            ResponseEntity.ok(cohorts)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/completed")
    fun getCompletedCohorts(): ResponseEntity<List<CohortDto>> {
        return try {
            val cohorts = cohortService.getCompletedCohorts()
            ResponseEntity.ok(cohorts)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{cohortId}/members")
    fun getCohortMembers(@PathVariable cohortId: UUID): ResponseEntity<List<UserSummaryDto>> {
        return try {
            val members = cohortService.getCohortMembers(cohortId)
            ResponseEntity.ok(members)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{cohortId}/members/{userId}")
    fun assignUserToCohort(
        @PathVariable cohortId: UUID,
        @PathVariable userId: UUID
    ): ResponseEntity<Void> {
        return try {
            cohortService.assignUserToCohort(cohortId, userId)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{cohortId}/members/{userId}")
    fun removeUserFromCohort(
        @PathVariable cohortId: UUID,
        @PathVariable userId: UUID
    ): ResponseEntity<Void> {
        return try {
            cohortService.removeUserFromCohort(cohortId, userId)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping
    fun createCohort(@Valid @RequestBody createRequest: CreateCohortRequest): ResponseEntity<CohortDto> {
        return try {
            val cohort = cohortService.createCohort(createRequest)
            ResponseEntity.ok(cohort)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{cohortId}")
    fun updateCohort(
        @PathVariable cohortId: UUID,
        @Valid @RequestBody updateRequest: UpdateCohortRequest
    ): ResponseEntity<CohortDto> {
        return try {
            val cohort = cohortService.updateCohort(cohortId, updateRequest)
            ResponseEntity.ok(cohort)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{cohortId}")
    fun deleteCohort(@PathVariable cohortId: UUID): ResponseEntity<Void> {
        return try {
            cohortService.deleteCohort(cohortId)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 