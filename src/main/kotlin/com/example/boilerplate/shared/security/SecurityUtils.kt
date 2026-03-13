package com.example.boilerplate.shared.security

import com.example.boilerplate.shared.dto.CurrentUser
import java.util.UUID
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun currentUser(): CurrentUser {

        val auth = SecurityContextHolder.getContext().authentication
        return auth?.principal as CurrentUser
    }

    fun currentUserId(): UUID {
        return currentUser().id
    }
}
