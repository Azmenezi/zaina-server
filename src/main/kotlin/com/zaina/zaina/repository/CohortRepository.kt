package com.zaina.zaina.repository

import com.zaina.zaina.entity.Cohort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface CohortRepository : JpaRepository<Cohort, UUID> {
    fun findByName(name: String): Cohort?
    
    @Query("SELECT c FROM Cohort c WHERE c.startDate <= :date AND c.endDate >= :date")
    fun findActiveCohorts(date: LocalDate = LocalDate.now()): List<Cohort>
    
    @Query("SELECT c FROM Cohort c WHERE c.endDate < :date")
    fun findCompletedCohorts(date: LocalDate = LocalDate.now()): List<Cohort>
    
    @Query("SELECT c FROM Cohort c WHERE c.startDate > :date")
    fun findUpcomingCohorts(date: LocalDate = LocalDate.now()): List<Cohort>
} 