package com.zaina.zaina.repository

import com.zaina.zaina.entity.User
import com.zaina.zaina.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findByRole(role: UserRole): List<User>
    fun findByCohortId(cohortId: UUID): List<User>
} 