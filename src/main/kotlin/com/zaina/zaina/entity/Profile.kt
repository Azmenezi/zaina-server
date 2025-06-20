package com.zaina.zaina.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "profiles")
data class Profile(
    @Id
    @Column(name = "user_id")
    val userId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column
    val position: String?,

    @Column
    val company: String?,

    @ElementCollection
    @CollectionTable(name = "profile_skills", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "skill")
    val skills: List<String> = listOf(),

    @Column(columnDefinition = "TEXT")
    val bio: String?,

    @Column(name = "image_url")
    val imageUrl: String?,

    @Column(name = "linkedin_url")
    val linkedinUrl: String?
) 