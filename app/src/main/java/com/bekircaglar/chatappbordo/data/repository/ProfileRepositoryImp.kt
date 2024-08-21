package com.bekircaglar.chatappbordo.data.repository

import android.util.Log
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Flow
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val databaseReference: DatabaseReference
) : ProfileRepository {
    override suspend fun getUserProfile(): kotlinx.coroutines.flow.Flow<Users?> =
        callbackFlow {

            val userId = auth.currentUser?.uid
            val userRef = databaseReference.database.getReference("Users").child(userId.toString())

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

    override suspend fun updateUserProfile() {


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