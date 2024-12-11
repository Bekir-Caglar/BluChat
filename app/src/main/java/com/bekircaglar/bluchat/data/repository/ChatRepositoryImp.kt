package com.bekircaglar.bluchat.data.repository

import android.util.Log
import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.GROUP
import com.bekircaglar.bluchat.utils.PRIVATE
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.STORED_USERS
import com.bekircaglar.bluchat.utils.USER_COLLECTION
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImp @Inject constructor(
    private var auth: FirebaseAuth, private var databaseReference: DatabaseReference
) : ChatsRepository {


    init {
        setUserOnlineStatus(auth.currentUser?.uid.toString())
    }


    override suspend fun searchContacts(query: String): Flow<Response<List<Users>>> = callbackFlow {
        val database = databaseReference.child(USER_COLLECTION)
        val currentUserRef =
            databaseReference.child(USER_COLLECTION).child(auth.currentUser?.uid.toString())

        val listener = object : ValueEventListener {
            override fun onDataChange(contactListSnapshot: DataSnapshot) {
                val contactIdList = contactListSnapshot.children.map { it.value as String }

                if (query.isEmpty() || query.isBlank()) {
                    database.get().addOnSuccessListener { allUsersSnapshot ->
                        val matchedUsers = mutableListOf<Users>()
                        for (userSnapshot in allUsersSnapshot.children) {
                            val user = userSnapshot.getValue(Users::class.java)
                            if (user != null && userSnapshot.key in contactIdList) {
                                matchedUsers.add(user)
                            }
                        }
                        trySend(Response.Success(matchedUsers))
                    }.addOnFailureListener { error ->
                        trySend(Response.Error(error.message.toString()))
                    }
                } else {
                    database.get().addOnSuccessListener { allUsersSnapshot ->
                        val matchedUsers = mutableListOf<Users>()
                        for (userSnapshot in allUsersSnapshot.children) {
                            val user = userSnapshot.getValue(Users::class.java)
                            val phoneNumber = user?.phoneNumber
                            if (phoneNumber?.contains(query) == true && userSnapshot.key in contactIdList) {
                                matchedUsers.add(user)
                            }
                        }
                        trySend(Response.Success(matchedUsers))
                    }.addOnFailureListener { error ->
                        trySend(Response.Error(error.message.toString()))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error(error.message))
            }
        }

        currentUserRef.child("contactsIdList").addValueEventListener(listener)
        awaitClose { currentUserRef.child("contactsIdList").removeEventListener(listener) }
    }

    override suspend fun createChatRoom(
        user1: String, user2: String, chatRoomId: String
    ): Flow<Response<String>> = callbackFlow {
        val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)
        val chatType: String = PRIVATE
        var chatRoomExists = false
        var existingChatRoomId = ""


        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (chatSnapshot in snapshot.children) {
                    val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                    if (chatRoom != null && chatRoom.users!!.containsAll(
                            listOf(
                                user1,
                                user2
                            )
                        ) && chatRoom.chatType == chatType
                    ) {
                        existingChatRoomId = chatSnapshot.key.toString()
                        chatRoomExists = true
                        break
                    }
                }

                if (!chatRoomExists) {
                    val chat = ChatRoom(
                        listOf(user1, user2),
                        chatRoomId,
                        chatType = chatType,
                        chatCreatedAt = System.currentTimeMillis()
                    )
                    databaseRef.child(chatRoomId).setValue(chat).addOnSuccessListener {
                        trySend(Response.Success(chatRoomId)).isSuccess
                        close()
                    }.addOnFailureListener { error ->
                        trySend(Response.Error("Failed to create chat room: ${error.message}")).isSuccess
                        close()
                    }
                } else {
                    trySend(Response.Error(existingChatRoomId)).isSuccess
                    close()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error("Database error: ${error.message}")).isSuccess
                close(error.toException())
            }
        }

        databaseRef.addValueEventListener(listener)
        awaitClose { databaseRef.removeEventListener(listener) }
    }

    override suspend fun getUserData(userId: String): Flow<Response<Users>> = callbackFlow {
        val database = databaseReference.database.getReference(USER_COLLECTION).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                if (user != null) {
                    trySend(Response.Success(user)).isSuccess
                } else {
                    trySend(Response.Error("User not found")).isSuccess
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error(error.message)).isSuccess
                close(error.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose()
    }

    override suspend fun getUsersChatList(): Flow<Response<List<ChatRoom>>> = callbackFlow {
        val currentUser = auth.currentUser?.uid.toString()
        val databaseReference =
            databaseReference.database.getReference(CHAT_COLLECTION).orderByChild(
                STORED_USERS
            )

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = mutableListOf<ChatRoom>()
                for (chatSnapshot in snapshot.children) {
                    val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)

                    if (chatRoom != null && chatRoom.users!!.contains(currentUser)) {
                        chatList.add(chatRoom)
                    }
                }
                trySend(Response.Success(chatList))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        })

        awaitClose()

    }

    override suspend fun saveSubId(subId: String) {
        val userSubIdRef =
            databaseReference.child(USER_COLLECTION).child(auth.currentUser?.uid.toString())
                .child("onesignalId")
        userSubIdRef.setValue(subId)
    }


    override suspend fun createGroupChatRoom(
        currentUser: String,
        groupMembers: List<String>,
        chatId: String,
        groupName: String,
        groupImg: String
    ): Flow<Response<String>> = callbackFlow {
        val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)

        val chatType: String = GROUP
        val newGroupMembers = groupMembers.toMutableList()
        newGroupMembers.add(currentUser)
        val chat = ChatRoom(
            users = newGroupMembers,
            chatId = chatId,
            chatName = groupName,
            chatImage = groupImg,
            chatType = chatType,
            chatAdminId = currentUser,
            chatCreatedAt = System.currentTimeMillis()
        )
        databaseRef.child(chatId).setValue(chat).addOnSuccessListener {
            trySend(Response.Success(chatId)).isSuccess
        }.addOnFailureListener { error ->
            trySend(Response.Error("Failed to create chat room: ${error.message}")).isSuccess
            close()
        }
        awaitClose()
    }


    private fun setUserOnlineStatus(userId: String) {
        val userStatusRef = databaseReference.child(USER_COLLECTION).child(userId).child("status")
        val lastSeenRef = databaseReference.child(USER_COLLECTION).child(userId).child("lastSeen")
        val connectedRef = databaseReference.database.getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false

                if (connected) {
                    userStatusRef.setValue(true)
                    userStatusRef.onDisconnect().setValue(false)
                    lastSeenRef.onDisconnect().setValue(System.currentTimeMillis())
                } else {
                    lastSeenRef.setValue(System.currentTimeMillis())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Connection listener was cancelled")
            }
        })
    }
}