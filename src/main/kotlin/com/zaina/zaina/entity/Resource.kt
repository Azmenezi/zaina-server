package com.zaina.zaina.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "resources")
data class Resource(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ResourceType,

    @Column(nullable = false)
    val url: String,

    @ElementCollection(targetClass = UserRole::class)
    @CollectionTable(name = "resource_target_roles", joinColumns = [JoinColumn(name = "resource_id")])
    @Enumerated(EnumType.STRING)
    @Column(name = "target_role")
    val targetRoles: List<UserRole> = listOf(),

    @Column
    val module: String?
)

enum class ResourceType {
    PDF, VIDEO, LINK
} 