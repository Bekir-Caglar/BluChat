package com.bekircaglar.bluchat.domain.repository

import com.bekircaglar.bluchat.Response
import kotlinx.coroutines.flow.Flow

interface AuthRepository  {

    fun isUserAuthenticatedInFirebase():Response<String>
    suspend fun signIn(email: String, password: String):Response<String>
    suspend fun signUp(email: String, password: String):Response<String>
    suspend fun createUser(name:String,surname:String,phoneNumber:String,email: String,userImageUrl:String?=""): Response<String>

    suspend fun checkPhoneNumber(phoneNumber:String): Flow<Response<String>>
}