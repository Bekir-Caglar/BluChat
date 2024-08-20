package com.bekircaglar.chatappbordo.data.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(private val auth:FirebaseAuth,private var databaseReference: DatabaseReference):AuthRepository {

    override fun isUserAuthenticatedInFirebase():Response<String>{
        return Response.Success(auth.currentUser?.uid?:"")

    }
    override suspend fun signIn(email: String, password: String):Response<String>{
        auth.signInWithEmailAndPassword(email, password).await()

        try {
            if(auth.currentUser!=null){
                return Response.Success(auth.currentUser.toString())
            }
            else{
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }

    }

    override suspend fun signUp(email: String, password: String):Response<String>{
        auth.createUserWithEmailAndPassword(email, password).await()

        try {
            if(auth.currentUser!=null){
                return Response.Success(auth.currentUser.toString())
            }
            else{
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }

    override suspend fun createUser(
        name: String,
        phoneNumber: String,
        email: String
    ): Response<String> {
        try {
            val user = hashMapOf(
                "name" to name,
                "phoneNumber" to phoneNumber,
                "email" to email,
                "uid" to auth.currentUser?.uid.toString(),
                "profileImageUrl" to "",
                "status" to true,
                "lastSeen" to "12:19"
            )
            databaseReference.child("Users").child(auth.currentUser?.uid.toString()).setValue(user).await()
            return Response.Success("User Created")
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }


}