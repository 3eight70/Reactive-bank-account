package com.learning.reactive.dao.user

import java.util.UUID

class UserDto (
    val id: UUID,
    val login: String,
    val email: String,
)