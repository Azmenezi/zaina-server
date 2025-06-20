package com.zaina.zaina.repository

import com.zaina.zaina.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProfileRepository : JpaRepository<Profile, UUID> {
    fun findByUserId(userId: UUID): Profile?
    
    @Query("SELECT p FROM Profile p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameContainingIgnoreCase(name: String): List<Profile>
    
    @Query("SELECT p FROM Profile p WHERE LOWER(p.company) LIKE LOWER(CONCAT('%', :company, '%'))")
    fun findByCompanyContainingIgnoreCase(company: String): List<Profile>
    
    @Query("""
        SELECT DISTINCT p FROM Profile p LEFT JOIN p.skills s 
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.company) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(s) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    fun searchProfiles(@Param("query") query: String): List<Profile>
} 