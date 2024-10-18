package com.bekircaglar.bluchat.domain.repository

interface PasswordValidator {
    fun isPasswordValid(password: String): Boolean
}