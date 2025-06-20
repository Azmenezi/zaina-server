package com.zaina.zaina.security

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zaina.zaina.entity.User
import com.zaina.zaina.entity.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class UserPrincipal(
    val id: UUID,
    val name: String?,
    private val email: String,
    @field:JsonIgnore
    private val password: String,
    val role: UserRole,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
            
            return UserPrincipal(
                id = user.id,
                name = null,
                email = user.email,
                password = user.passwordHash,
                role = user.role,
                authorities = authorities
            )
        }
    }

    override fun getUsername(): String = email
    override fun getPassword(): String = password
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
} 