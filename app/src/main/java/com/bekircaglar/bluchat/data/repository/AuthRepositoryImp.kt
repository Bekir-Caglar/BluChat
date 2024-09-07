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
                profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/profileImages%2F1000000026?alt=media&token=f49659f9-8128-4400-af7f-a2bc4a938eaf",
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

    override suspend fun checkPhoneNumber(phoneNumber: String): Response<String> {
        return suspendCancellableCoroutine { continuation ->
            val database = databaseReference.database.getReference(USER_COLLECTION)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var phoneExists = false
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(Users::class.java)
                        if (user?.phoneNumber.toString().equals(phoneNumber)) {
                            phoneExists = true
                            break
                        }
                    }
                    if (phoneExists) {
                        continuation.resume(Response.Error("Phone Number Exists")) { }
                    } else {
                        continuation.resume(Response.Success("Phone Number Does Not Exist")) { }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Response.Error(error.message)) { }
                }
            })
        }
    }

}