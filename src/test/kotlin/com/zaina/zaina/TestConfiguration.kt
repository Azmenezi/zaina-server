package com.zaina.zaina

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.dto.LoginRequest
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@TestConfiguration
class TestConfig {
    
    @Bean
    @Primary
    fun testObjectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()
}

class TestUtils {
    companion object {
        
        fun getAuthToken(mockMvc: MockMvc, objectMapper: ObjectMapper, email: String, password: String): String {
            val loginRequest = LoginRequest(email = email, password = password)
            
            val result = mockMvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest))
            )
                .andExpect(status().isOk)
                .andReturn()
            
            val response = objectMapper.readTree(result.response.contentAsString)
            return response.get("token").asText()
        }
        
        fun createMockAuthentication(userId: UUID, role: UserRole): UsernamePasswordAuthenticationToken {
            val authorities = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
            return UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                authorities
            )
        }
        
        // Test user credentials
        const val APPLICANT_EMAIL = "applicant@example.com"
        const val PARTICIPANT_EMAIL = "participant@example.com" 
        const val ALUMNA_EMAIL = "alumna@example.com"
        const val TEST_PASSWORD = "password123"
    }
} 