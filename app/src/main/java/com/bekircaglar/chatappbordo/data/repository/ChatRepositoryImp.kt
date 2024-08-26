package com.bekircaglar.chatappbordo.data.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.repository.ChatsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImp @Inject constructor(
    private var auth: FirebaseAuth,
    private var databaseReference: DatabaseReference
):ChatsRepository {

    override suspend fun searchContacts(query: String): Response<List<Users>> {
        return suspendCancellableCoroutine { continuation ->
            val database = databaseReference.database.getReference("Users")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val matchedUsers = mutableListOf<Users>()

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(Users::class.java)
                        if (user?.phoneNumber?.contains(query) == true) {
                            matchedUsers.add(user)
                        }
                    }

                    if (matchedUsers.isNotEmpty()) {
                        continuation.resume(Response.Success(matchedUsers)) { }
                    } else {
                        continuation.resume(Response.Error("No users found with the given query")) { }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Response.Error(error.message)) { }
                }
            })
        }
    }
}