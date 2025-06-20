package com.zaina.zaina.service

import com.zaina.zaina.dto.JwtResponse
import com.zaina.zaina.dto.LoginRequest
import com.zaina.zaina.dto.RegisterRequest
import com.zaina.zaina.entity.Profile
import com.zaina.zaina.entity.User
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.repository.UserRepository
import com.zaina.zaina.security.JwtUtils
import com.zaina.zaina.security.UserPrincipal
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils
) {

    fun registerUser(registerRequest: RegisterRequest): JwtResponse {
        if (userRepository.existsByEmail(registerRequest.email)) {
            throw RuntimeException("Error: Email is already in use!")
        }

        // Create new user
        val user = User(
            email = registerRequest.email,
            passwordHash = passwordEncoder.encode(registerRequest.password),
            role = UserRole.APPLICANT, // Default role for new registrations
            cohortId = registerRequest.cohortId
        )

        val savedUser = userRepository.save(user)

        // Create profile for the user
        val profile = Profile(
            userId = savedUser.id,
            name = registerRequest.name,
            position = registerRequest.position,
            company = registerRequest.company,
            bio = registerRequest.bio,
            imageUrl = null,
            linkedinUrl = null
        )
        
        val savedProfile = profileRepository.save(profile)

        // Generate JWT token
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                registerRequest.email,
                registerRequest.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)

        val userPrincipal = authentication.principal as UserPrincipal

        return JwtResponse(
            token = jwt,
            id = userPrincipal.id,
            email = userPrincipal.username,
            role = userPrincipal.role,
            name = savedProfile.name
        )
    }

    fun authenticateUser(loginRequest: LoginRequest): JwtResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)

        val userPrincipal = authentication.principal as UserPrincipal
        
        // Get the user's profile to include their name
        val profile = profileRepository.findByUserId(userPrincipal.id)

        return JwtResponse(
            token = jwt,
            id = userPrincipal.id,
            email = userPrincipal.username,
            role = userPrincipal.role,
            name = profile?.name
        )
    }
} 