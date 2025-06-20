package com.zaina.zaina.service

import com.zaina.zaina.dto.CreateResourceRequest
import com.zaina.zaina.dto.ResourceDto
import com.zaina.zaina.dto.UpdateResourceRequest
import com.zaina.zaina.entity.Resource
import com.zaina.zaina.entity.ResourceType
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.mapper.ResourceMapper
import com.zaina.zaina.repository.ResourceRepository
import com.zaina.zaina.security.UserPrincipal
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ResourceService(
    private val resourceRepository: ResourceRepository
) {

    fun getAllResources(): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        // Filter resources based on user role
        return when (currentUser.role) {
            UserRole.APPLICANT -> {
                // Applicants can only see resources that don't have target roles or include APPLICANT
                resourceRepository.findAll()
                    .filter { resource -> 
                        resource.targetRoles.isEmpty() || resource.targetRoles.contains(UserRole.APPLICANT) 
                    }
                    .map { ResourceMapper.toResourceDto(it) }
            }
            UserRole.PARTICIPANT -> {
                resourceRepository.findByTargetRoles(listOf(UserRole.PARTICIPANT, UserRole.ALUMNA))
                    .map { ResourceMapper.toResourceDto(it) }
            }
            UserRole.ALUMNA -> {
                resourceRepository.findByTargetRoles(listOf(UserRole.PARTICIPANT, UserRole.ALUMNA))
                    .map { ResourceMapper.toResourceDto(it) }
            }
            UserRole.MENTOR -> {
                resourceRepository.findByTargetRoles(listOf(UserRole.PARTICIPANT, UserRole.ALUMNA, UserRole.MENTOR))
                    .map { ResourceMapper.toResourceDto(it) }
            }
        }
    }

    fun getAllResources(pageable: Pageable): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        // Filter resources based on user role with pagination
        return when (currentUser.role) {
            UserRole.APPLICANT -> {
                // Applicants can only see resources that don't have target roles or include APPLICANT
                resourceRepository.findAll(pageable).content
                    .filter { resource -> 
                        resource.targetRoles.isEmpty() || resource.targetRoles.contains(UserRole.APPLICANT) 
                    }
                    .map { ResourceMapper.toResourceDto(it) }
            }
            UserRole.PARTICIPANT -> {
                resourceRepository.findByTargetRoles(listOf(UserRole.PARTICIPANT, UserRole.ALUMNA))
                    .map { ResourceMapper.toResourceDto(it) }
            }
            UserRole.ALUMNA -> {
                resourceRepository.findByTargetRoles(listOf(UserRole.PARTICIPANT, UserRole.ALUMNA))
                    .map { ResourceMapper.toResourceDto(it) }
            }
            UserRole.MENTOR -> {
                resourceRepository.findByTargetRoles(listOf(UserRole.PARTICIPANT, UserRole.ALUMNA, UserRole.MENTOR))
                    .map { ResourceMapper.toResourceDto(it) }
            }
        }
    }

    fun getResourceById(resourceId: UUID): ResourceDto {
        val resource = resourceRepository.findById(resourceId)
            .orElseThrow { RuntimeException("Resource not found") }
        
        val currentUser = getCurrentUserPrincipal()
        
        // Check if user has access to this resource
        if (resource.targetRoles.isNotEmpty() && !resource.targetRoles.contains(currentUser.role)) {
            throw RuntimeException("Access denied: You don't have permission to access this resource")
        }
        
        return ResourceMapper.toResourceDto(resource)
    }

    fun getResourcesByType(type: ResourceType): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        return resourceRepository.findByType(type)
            .filter { resource ->
                resource.targetRoles.isEmpty() || resource.targetRoles.contains(currentUser.role)
            }
            .map { ResourceMapper.toResourceDto(it) }
    }

    fun getResourcesByType(type: ResourceType, pageable: Pageable): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        return resourceRepository.findByType(type, pageable).content
            .filter { resource ->
                resource.targetRoles.isEmpty() || resource.targetRoles.contains(currentUser.role)
            }
            .map { ResourceMapper.toResourceDto(it) }
    }

    fun getResourcesByModule(module: String): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        return resourceRepository.findByModuleAndTargetRole(module, currentUser.role)
            .map { ResourceMapper.toResourceDto(it) }
    }

    fun getResourcesByModule(module: String, pageable: Pageable): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        return resourceRepository.findByModuleAndTargetRole(module, currentUser.role, pageable).content
            .map { ResourceMapper.toResourceDto(it) }
    }

    fun searchResources(query: String): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        return resourceRepository.findByTitleContainingIgnoreCase(query)
            .filter { resource ->
                resource.targetRoles.isEmpty() || resource.targetRoles.contains(currentUser.role)
            }
            .map { ResourceMapper.toResourceDto(it) }
    }

    fun searchResources(query: String, pageable: Pageable): List<ResourceDto> {
        val currentUser = getCurrentUserPrincipal()
        
        return resourceRepository.findByTitleContainingIgnoreCase(query, pageable).content
            .filter { resource ->
                resource.targetRoles.isEmpty() || resource.targetRoles.contains(currentUser.role)
            }
            .map { ResourceMapper.toResourceDto(it) }
    }

    fun createResource(createRequest: CreateResourceRequest): ResourceDto {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants, alumni, and mentors can create resources (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants, alumni, and mentors can create resources")
        }

        val resource = Resource(
            title = createRequest.title,
            description = createRequest.description,
            type = createRequest.type,
            url = createRequest.url,
            targetRoles = createRequest.targetRoles,
            module = createRequest.module
        )

        val savedResource = resourceRepository.save(resource)
        return ResourceMapper.toResourceDto(savedResource)
    }

    fun updateResource(resourceId: UUID, updateRequest: UpdateResourceRequest): ResourceDto {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants, alumni, and mentors can update resources (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants, alumni, and mentors can update resources")
        }

        val existingResource = resourceRepository.findById(resourceId)
            .orElseThrow { RuntimeException("Resource not found") }

        val updatedResource = Resource(
            id = existingResource.id,
            title = updateRequest.title,
            description = updateRequest.description,
            type = updateRequest.type,
            url = updateRequest.url,
            targetRoles = updateRequest.targetRoles,
            module = updateRequest.module
        )

        val savedResource = resourceRepository.save(updatedResource)
        return ResourceMapper.toResourceDto(savedResource)
    }

    fun deleteResource(resourceId: UUID) {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants, alumni, and mentors can delete resources (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants, alumni, and mentors can delete resources")
        }

        resourceRepository.deleteById(resourceId)
    }

    private fun getCurrentUserPrincipal(): UserPrincipal {
        return SecurityContextHolder.getContext().authentication.principal as UserPrincipal
    }
} 