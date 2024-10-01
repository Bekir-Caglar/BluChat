package com.bekircaglar.bluchat.data.repository

import android.util.Log
import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.GROUP
import com.bekircaglar.bluchat.utils.PRIVATE
import com.bekircaglar.bluchat.Response
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

    override suspend fun searchContacts(query: String): Response<List<Users>> {
        return suspendCancellableCoroutine { continuation ->
            val database = databaseReference.database.getReference(USER_COLLECTION)
            var isResumed = false

            if (query.isEmpty() || query.isBlank()) {
                GlobalScope.launch {
                    val matchedUsers = mutableListOf<Users>()
                    val result = database.get().await()
                    for (snapshot in result.children) {
                        val user = snapshot.getValue(Users::class.java)
                        if (user != null) {
                            matchedUsers.add(user)
                        }
                    }
                    if (!isResumed) {
                        isResumed = true
                        continuation.resume(Response.Success(matchedUsers)) { }
                    }
                }
                return@suspendCancellableCoroutine
            }

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val matchedUsers = mutableListOf<Users>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(Users::class.java)
                        if (user?.phoneNumber?.contains(query) == true) {
                            matchedUsers.add(user)
                        }
                    }
                    if (!isResumed) {
                        isResumed = true
                        continuation.resume(Response.Success(matchedUsers)) { }
                        database.removeEventListener(this)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!isResumed) {
                        isResumed = true
                        continuation.resume(Response.Error(error.message)) { }
                        database.removeEventListener(this)
                    }
                }
            }

            database.addValueEventListener(listener)
        }
    }
    override suspend fun createChatRoom(
        user1: String, user2: String, chatRoomId: String
    ): Flow<Response<String>> = callbackFlow {
        val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)
        val chatType: String = PRIVATE

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var chatRoomExists = false
                var existingChatRoomId = ""

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
                    val chat = ChatRoom(listOf(user1, user2), chatRoomId, chatType = chatType)
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


    override suspend fun createGroupChatRoom(
        currentUser: String,
        groupMembers: List<String>,
        chatId: String,
        groupName: String,
        groupImg: String
    ): Flow<Response<String>> = callbackFlow {
        val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatType: String = GROUP
                val newGroupMembers = groupMembers.toMutableList()
                newGroupMembers.add(currentUser)

                val chat = ChatRoom(
                    newGroupMembers,
                    chatId,
                    groupName,
                    groupImg,
                    chatType,
                    chatAdminId = currentUser
                )
                databaseRef.child(chatId).setValue(chat).addOnSuccessListener {
                    trySend(Response.Success(chatId)).isSuccess
                }.addOnFailureListener { error ->
                    trySend(Response.Error("Failed to create chat room: ${error.message}")).isSuccess
                    close()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error("Database error: ${error.message}")).isSuccess
                close(error.toException())
            }
        }

        databaseRef.addValueEventListener(listener)
        awaitClose()
    }


    private fun setUserOnlineStatus(userId: String) {
        val userStatusRef = databaseReference.child(USER_COLLECTION).child(userId).child("status")
        val connectedRef = databaseReference.database.getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false

                if (connected) {
                    userStatusRef.setValue(true)
                    userStatusRef.onDisconnect().setValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Connection listener was cancelled")
            }
        })
    }
//    fun setLastSeen(userId: String) {
//        val lastSeenRef = databaseReference.child(USER_COLLECTION).child(userId).child("lastSeen")
//        lastSeenRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
//    }
}