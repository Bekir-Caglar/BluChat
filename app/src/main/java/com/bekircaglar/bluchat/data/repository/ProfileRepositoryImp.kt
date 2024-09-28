package com.bekircaglar.bluchat.data.repository

import android.content.Context
import android.net.Uri
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.USER_COLLECTION
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val storageReference: FirebaseStorage
) : ProfileRepository {
    override suspend fun getUserProfile(): Flow<Response<Users?>> =
        callbackFlow {

            val userId = auth.currentUser?.uid
            val userRef = databaseReference.database.getReference(USER_COLLECTION).child(userId.toString())

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    trySend(Response.Success(data = user)).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            userRef.addValueEventListener(listener)
            awaitClose { userRef.removeEventListener(listener) }

        }

    override suspend fun updateUserProfile(user: Users): Response<String> {

        val userId = auth.currentUser?.uid
        val userRef = databaseReference.database.getReference(USER_COLLECTION).child(userId.toString())

        try {
            userRef.setValue(user).await()
            return Response.Success("User Updated")
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }

    }
    override suspend fun uploadImage(uri: Uri): kotlinx.coroutines.flow.Flow<Response<String>>  = flow{
        val randomUUID = java.util.UUID.randomUUID().toString()
        val storageReference = storageReference.reference.child("profileImages/${randomUUID}")
        val uploadTask = storageReference.putFile(uri)

        val downloadUrl = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl

        }.await()
        emit(Response.Success(downloadUrl.toString()))
    }

    override suspend fun signOut(context:Context): Response<String> {
        auth.signOut()

        try {
            if (auth.currentUser == null) {
                val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
                googleSignInClient.signOut().await()
                return Response.Success("SignOut")
            } else {
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }
}