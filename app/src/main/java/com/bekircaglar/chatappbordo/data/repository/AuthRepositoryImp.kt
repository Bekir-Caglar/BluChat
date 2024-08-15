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
    override fun isUserAuthenticatedInFirebase(): Flow<Response<Boolean>> = flow {
        try {
            emit(Response.Loading)
            if (auth.currentUser != null) {
                emit(Response.Success(true))
            } else {
                emit(Response.Success(false))
            }
        } catch (e: Exception) {
            emit(Response.Success(false))
        }

    }
    override suspend fun signIn(email: String, password: String): Flow<Response<Boolean>> = callbackFlow {

        try {
            this@callbackFlow.trySendBlocking(Response.Loading)
            auth.signInWithEmailAndPassword(email, password).apply {
                this.await()
                if (this.isSuccessful) {
                    this@callbackFlow.trySendBlocking(Response.Success(true))
                } else {
                    this@callbackFlow.trySendBlocking(Response.Success(false))
                }
            }
        } catch (e: Exception) {
            this@callbackFlow.trySendBlocking(Response.Error(e.message ?: "Unexpected error"))
        }

        awaitClose {
            channel.close()
            cancel()
        }

    }

    override suspend fun signUp(email: String, password: String): Flow<Response<Boolean>> = flow{

    }

    override suspend fun signOut(): Flow<Response<Boolean>> = flow {

    }
}