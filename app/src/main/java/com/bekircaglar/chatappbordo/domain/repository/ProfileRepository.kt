package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response

interface ProfileRepository {
    suspend fun getUserProfile()
    suspend fun updateUserProfile()
    suspend fun signOut(): Response<String>
}