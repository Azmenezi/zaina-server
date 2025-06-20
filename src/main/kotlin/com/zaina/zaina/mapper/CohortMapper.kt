package com.zaina.zaina.mapper

import com.zaina.zaina.dto.CohortDto
import com.zaina.zaina.dto.CohortStatus
import com.zaina.zaina.entity.Cohort
import java.time.LocalDate

object CohortMapper {
    
    fun toCohortDto(cohort: Cohort, participantCount: Int = 0): CohortDto {
        val status = when {
            cohort.startDate > LocalDate.now() -> CohortStatus.UPCOMING
            cohort.endDate < LocalDate.now() -> CohortStatus.COMPLETED
            else -> CohortStatus.ACTIVE
        }
        
        return CohortDto(
            id = cohort.id,
            name = cohort.name,
            startDate = cohort.startDate,
            endDate = cohort.endDate,
            participantCount = participantCount,
            status = status
        )
    }
} 