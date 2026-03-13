package com.example.boilerplate.shared.audit

import com.example.boilerplate.shared.dto.CurrentUser
import java.util.Optional
import java.util.UUID
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("auditorProvider")
class AuditorProvider : AuditorAware<UUID> {

    override fun getCurrentAuditor(): Optional<UUID> {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: return Optional.empty()
        if (!authentication.isAuthenticated) return Optional.empty()
        val userId = (authentication.principal as? CurrentUser)?.id ?: return Optional.empty()
        return Optional.of(userId)
    }
}
