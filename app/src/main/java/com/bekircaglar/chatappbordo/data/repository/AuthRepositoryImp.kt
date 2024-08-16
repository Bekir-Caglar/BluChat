package com.bekircaglar.chatappbordo.data.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(private val auth:FirebaseAuth):AuthRepository {
    override fun isUserAuthenticatedInFirebase():Response<String>{



        return Response.Success(auth.currentUser?.uid?:"")

    }
    override suspend fun signIn(email: String, password: String):Response<String>{
        auth.signInWithEmailAndPassword(email, password).await()
        if(auth.currentUser!=null){
            return Response.Success(auth.currentUser.toString())
        }
        else{
            return Response.Error("Error")
        }

    }

    override suspend fun signUp(email: String, password: String):Response<String>{
        auth.createUserWithEmailAndPassword(email, password).await()

        if(auth.currentUser!=null){
            return Response.Success(auth.currentUser.toString())
        }
        else{
            return Response.Error("Error")
        }
    }

    override suspend fun signOut():Response<String>{



        return Response.Success("")
    }
}