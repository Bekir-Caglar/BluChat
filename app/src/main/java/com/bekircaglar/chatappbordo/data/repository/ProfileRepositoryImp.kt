package com.bekircaglar.chatappbordo.data.repository

import android.net.Uri
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.USER_COLLECTION
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val storageReference: FirebaseStorage
) : ProfileRepository {
    override suspend fun getUserProfile(): kotlinx.coroutines.flow.Flow<Users?> =
        callbackFlow {

            val userId = auth.currentUser?.uid
            val userRef = databaseReference.database.getReference(USER_COLLECTION).child(userId.toString())

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    trySend(user).isSuccess
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
        val storageReference = storageReference.reference.child("profileImages/${uri.lastPathSegment}")
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

    override suspend fun signOut(): Response<String> {
        auth.signOut()
        try {
            if (auth.currentUser == null) {
                return Response.Success("SignOut")
            } else {
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }
}