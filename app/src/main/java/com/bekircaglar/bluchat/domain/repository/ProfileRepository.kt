package com.bekircaglar.bluchat.domain.repository

import android.content.Context
import android.net.Uri
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.Users
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUserProfile(): Flow<Response<Users?>>
    suspend fun updateUserProfile(user: Users): Response<String>
    suspend fun uploadImage(uri: Uri): Flow<Response<String>>
    suspend fun signOut(context: Context): Response<String>
}