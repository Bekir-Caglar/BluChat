package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response

interface AuthRepository  {

    fun isUserAuthenticatedInFirebase():Response<String>
    suspend fun signIn(email: String, password: String):Response<String>
    suspend fun signUp(email: String, password: String):Response<String>
    suspend fun createUser(name:String,surname:String,phoneNumber:String,email: String,): Response<String>

    suspend fun checkPhoneNumber(phoneNumber:String): Response<String>
}