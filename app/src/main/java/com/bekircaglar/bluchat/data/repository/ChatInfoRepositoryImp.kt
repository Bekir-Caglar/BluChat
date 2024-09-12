package com.bekircaglar.bluchat.data.repository

import android.util.Log
import com.bekircaglar.bluchat.CHAT_COLLECTION
import com.bekircaglar.bluchat.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.STORED_USERS
import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatInfoRepositoryImp @Inject constructor(
    private val databaseRef : DatabaseReference



):ChatInfoRepository {
    override suspend fun kickUser(userId: String, chatId: String) {
        val chatRef = databaseRef.database.getReference(CHAT_COLLECTION).child(chatId).child(STORED_USERS)

        chatRef.orderByValue().equalTo(userId).addListenerForSingleValueEvent(object:
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

        val chatRef = databaseRef.database.getReference(CHAT_COLLECTION).child(chatId).child(STORED_USERS)

        chatRef.orderByValue().equalTo(userId).addListenerForSingleValueEvent(object:
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


    override suspend fun addParticipant(chatId: String, userIdList: List<String?>) {

        val chatRef = databaseRef.database.getReference(CHAT_COLLECTION).child(chatId).child(STORED_USERS)
        chatRef.setValue(userIdList)
    }

    private fun deleteMessages(chatId: String) {
        val chatRef = databaseRef.database.getReference(MESSAGE_COLLECTION).child(chatId)
        chatRef.removeValue()
    }


}