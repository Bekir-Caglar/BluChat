package com.bekircaglar.bluchat.data.repository

import android.util.Log
import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.utils.STORED_MESSAGES
import com.bekircaglar.bluchat.utils.STORED_USERS
import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChatInfoRepositoryImp @Inject constructor(
    private val databaseRef: DatabaseReference


) : ChatInfoRepository {
    override suspend fun kickUser(userId: String, chatId: String) {
        val chatRef =
            databaseRef.database.getReference(CHAT_COLLECTION).child(chatId).child(STORED_USERS)

        chatRef.orderByValue().equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    userSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Kullanıcı silinirken hata oluştu: ${error.message}")
            }
        })
    }

    override suspend fun deleteGroup(chatId: String) {

        val chatRef = databaseRef.database.getReference(CHAT_COLLECTION).child(chatId)
        chatRef.removeValue()
        deleteMessages(chatId)
    }

    override suspend fun leaveChat(chatId: String, userId: String) {

        val chatRef =
            databaseRef.database.getReference(CHAT_COLLECTION).child(chatId).child(STORED_USERS)

        chatRef.orderByValue().equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    userSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Kullanıcı silinirken hata oluştu: ${error.message}")
            }
        })
    }

    override suspend fun getChatImages(chatId: String): Flow<Response<List<String>>> =
        callbackFlow {
            val chatRef = databaseRef.database.getReference(MESSAGE_COLLECTION).child(chatId).child(
                STORED_MESSAGES
            )
            chatRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val imageUrlList = mutableListOf<String>()
                    for (messageSnapshot in snapshot.children) {
                        val imageUrl = messageSnapshot.child("imageUrl").value.toString()
                        if (imageUrl.isNotBlank()) {
                            imageUrlList.add(imageUrl)
                        }
                    }
                    trySend(Response.Success(imageUrlList))

                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

            awaitClose()
        }


    override suspend fun addParticipant(chatId: String, userIdList: List<String?>) {

        val chatRef =
            databaseRef.database.getReference(CHAT_COLLECTION).child(chatId).child(STORED_USERS)
        chatRef.setValue(userIdList)
    }

    override suspend fun updateChatInfo(chatId: String, chatName: String, chatImageUrl: String) {
        val chatRef = databaseRef.database.getReference(CHAT_COLLECTION).child(chatId)
        chatRef.child("chatName").setValue(chatName)
        chatRef.child("chatImage").setValue(chatImageUrl)
    }

    private fun deleteMessages(chatId: String) {
        val chatRef = databaseRef.database.getReference(MESSAGE_COLLECTION).child(chatId)
        chatRef.removeValue()
    }


}