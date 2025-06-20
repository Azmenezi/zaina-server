package com.zaina.zaina.controller

import com.zaina.zaina.dto.CreateResourceRequest
import com.zaina.zaina.dto.ResourceDto
import com.zaina.zaina.dto.UpdateResourceRequest
import com.zaina.zaina.entity.ResourceType
import com.zaina.zaina.service.ResourceService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class ResourceController(
    private val resourceService: ResourceService
) {

    @GetMapping
    fun getAllResources(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<ResourceDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val resources = resourceService.getAllResources(pageable)
            ResponseEntity.ok(resources)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{resourceId}")
    fun getResourceById(@PathVariable resourceId: UUID): ResponseEntity<ResourceDto> {
        return try {
            val resource = resourceService.getResourceById(resourceId)
            ResponseEntity.ok(resource)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/type/{type}")
    fun getResourcesByType(
        @PathVariable type: ResourceType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<ResourceDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val resources = resourceService.getResourcesByType(type, pageable)
            ResponseEntity.ok(resources)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/module/{module}")
    fun getResourcesByModule(
        @PathVariable module: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<ResourceDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val resources = resourceService.getResourcesByModule(module, pageable)
            ResponseEntity.ok(resources)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/search")
    fun searchResources(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<ResourceDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val resources = resourceService.searchResources(query, pageable)
            ResponseEntity.ok(resources)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping
    fun createResource(@Valid @RequestBody createRequest: CreateResourceRequest): ResponseEntity<ResourceDto> {
        return try {
            val resource = resourceService.createResource(createRequest)
            ResponseEntity.ok(resource)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{resourceId}")
    fun updateResource(
        @PathVariable resourceId: UUID,
        @Valid @RequestBody updateRequest: UpdateResourceRequest
    ): ResponseEntity<ResourceDto> {
        return try {
            val resource = resourceService.updateResource(resourceId, updateRequest)
            ResponseEntity.ok(resource)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{resourceId}")
    fun deleteResource(@PathVariable resourceId: UUID): ResponseEntity<Void> {
        return try {
            resourceService.deleteResource(resourceId)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 