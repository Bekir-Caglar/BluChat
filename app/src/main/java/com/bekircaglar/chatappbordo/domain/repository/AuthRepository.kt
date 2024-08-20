package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AuthRepository  {

    fun isUserAuthenticatedInFirebase():Response<String>
    suspend fun signIn(email: String, password: String):Response<String>
    suspend fun signUp(email: String, password: String):Response<String>
    suspend fun createUser(name:String,phoneNumber:String,email: String,): Response<String>
}