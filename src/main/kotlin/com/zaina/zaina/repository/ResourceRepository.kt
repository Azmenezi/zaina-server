package com.zaina.zaina.repository

import com.zaina.zaina.entity.Resource
import com.zaina.zaina.entity.ResourceType
import com.zaina.zaina.entity.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ResourceRepository : JpaRepository<Resource, UUID> {
    fun findByType(type: ResourceType): List<Resource>
    fun findByType(type: ResourceType, pageable: Pageable): Page<Resource>
    
    fun findByModule(module: String): List<Resource>
    fun findByModule(module: String, pageable: Pageable): Page<Resource>
    
    @Query("SELECT r FROM Resource r JOIN r.targetRoles tr WHERE tr = :role")
    fun findByTargetRole(role: UserRole): List<Resource>
    
    @Query("SELECT r FROM Resource r JOIN r.targetRoles tr WHERE tr IN :roles")
    fun findByTargetRoles(roles: List<UserRole>): List<Resource>
    
    @Query("SELECT r FROM Resource r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    fun findByTitleContainingIgnoreCase(title: String): List<Resource>
    
    @Query("SELECT r FROM Resource r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<Resource>
    
    @Query("SELECT r FROM Resource r WHERE r.module = :module AND :role MEMBER OF r.targetRoles")
    fun findByModuleAndTargetRole(module: String, role: UserRole): List<Resource>
    
    @Query("SELECT r FROM Resource r WHERE r.module = :module AND :role MEMBER OF r.targetRoles")
    fun findByModuleAndTargetRole(module: String, role: UserRole, pageable: Pageable): Page<Resource>
} 