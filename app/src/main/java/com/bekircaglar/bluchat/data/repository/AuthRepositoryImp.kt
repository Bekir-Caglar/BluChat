package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.USER_COLLECTION
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private var databaseReference: DatabaseReference
) : AuthRepository {

    override fun isUserAuthenticatedInFirebase(): Response<String> {
        return Response.Success(auth.currentUser?.uid ?: "")

    }

    override suspend fun signIn(email: String, password: String): Response<String> {
        auth.signInWithEmailAndPassword(email, password).await()
        try {
            if (auth.currentUser != null) {
                return Response.Success(auth.currentUser.toString())
            } else {
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }

    }

    override suspend fun signUp(email: String, password: String): Response<String> {
        auth.createUserWithEmailAndPassword(email, password).await()

        try {
            if (auth.currentUser != null) {
                return Response.Success(auth.currentUser.toString())
            } else {
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }

    override suspend fun createUser(
        name: String,
        surname:String,
        phoneNumber: String,
        email: String
    ): Response<String> {
        try {
            val user = Users(
                name = name,
                surname = surname,
                phoneNumber = phoneNumber,
                email = email,
                uid = auth.currentUser?.uid.toString(),
                profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/profileImages%2FSubject.png?alt=media&token=0fcc75d0-a3e8-49f3-926a-e0cb8db0e6b8",
                status = false,
                lastSeen = "12:19"
            )
            databaseReference.child(USER_COLLECTION).child(auth.currentUser?.uid.toString()).setValue(user)
                .await()
            return Response.Success("User Created")
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }

    override suspend fun checkPhoneNumber(phoneNumber: String): Flow<Response<String>> = callbackFlow {
        val database = databaseReference.database.getReference(USER_COLLECTION)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var phoneExists = false
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    if (user?.phoneNumber.toString() == phoneNumber) {
                        phoneExists = true
                        break
                    }
                }
                if (phoneExists) {
                    trySend(Response.Error("Phone Number Exists")).isSuccess
                } else {
                    trySend(Response.Success("Phone Number Does Not Exist")).isSuccess
                }
                close()
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error(error.message)).isSuccess
                close(error.toException())
            }
        }

        database.addListenerForSingleValueEvent(listener)
        awaitClose { database.removeEventListener(listener) }
    }

}