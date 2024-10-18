package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.domain.repository.PasswordValidator

class PasswordValidatorImpl : PasswordValidator {
    override fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$".toRegex()
        return passwordRegex.matches(password)
    }
}