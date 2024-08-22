package com.bekircaglar.chatappbordo.domain.repository

import android.net.Uri
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUserProfile(): Flow<Users?>
    suspend fun updateUserProfile(user: Users):Response<String>
    suspend fun uploadImage(uri: Uri): Flow<Response<String>>
    suspend fun signOut(): Response<String>
}