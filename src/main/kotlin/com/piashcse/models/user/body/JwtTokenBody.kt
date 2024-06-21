package com.piashcse.models.user.body

import io.ktor.server.auth.Principal

data class JwtTokenBody(
    val userId: String,
    val email: String,
    val userType: String,
) : Principal