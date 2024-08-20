package com.bekircaglar.chatappbordo.data.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(private val auth : FirebaseAuth):ProfileRepository {
    override suspend fun getUserProfile() {


    }

    override suspend fun updateUserProfile() {


    }

    override suspend fun signOut(): Response<String> {
        auth.signOut()
        try {
            if(auth.currentUser==null){
                return Response.Success("SignOut")
            }
            else{
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }
}