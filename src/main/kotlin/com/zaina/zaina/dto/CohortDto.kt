package com.zaina.zaina.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.*

data class CohortDto(
    val id: UUID,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val participantCount: Int = 0,
    val status: CohortStatus
)

data class CreateCohortRequest(
    @field:NotBlank
    val name: String,
    @field:NotNull
    val startDate: LocalDate,
    @field:NotNull
    val endDate: LocalDate
)

data class UpdateCohortRequest(
    @field:NotBlank
    val name: String,
    @field:NotNull
    val startDate: LocalDate,
    @field:NotNull
    val endDate: LocalDate
)

enum class CohortStatus {
    UPCOMING, ACTIVE, COMPLETED
} 