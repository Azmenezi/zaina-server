package com.zaina.zaina.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @Column(name = "cohort_id")
    val cohortId: UUID? = null
)

enum class UserRole {
    APPLICANT, PARTICIPANT, ALUMNA, MENTOR
} 