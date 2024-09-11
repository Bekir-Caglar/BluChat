package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.CHAT_COLLECTION
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.STORED_USERS
import com.bekircaglar.bluchat.USER_COLLECTION
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
    private var auth: FirebaseAuth,
    private var databaseReference: DatabaseReference
) : ChatsRepository {

    override suspend fun searchContacts(query: String): Response<List<Users>> {
        return suspendCancellableCoroutine { continuation ->

            val database = databaseReference.database.getReference(USER_COLLECTION)

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
                    continuation.resume(Response.Success(matchedUsers)) { }
                }
                return@suspendCancellableCoroutine
            }

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

    override suspend fun createChatRoom(
        user1: String,
        user2: String,
        chatRoomId: String
    ): Response<String> {
        return suspendCancellableCoroutine { continuation ->
            val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)

            val chatType: String = "private"
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var chatRoomExists = false
                    var existingChatRoomId = ""

                    for (chatSnapshot in snapshot.children) {
                        val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                        if (chatRoom != null && chatRoom.users!!.containsAll(
                                listOf(user1, user2)
                            )
                        ) {
                            existingChatRoomId = chatSnapshot.key.toString()
                            chatRoomExists = true
                            break
                        }
                    }
                    if (!chatRoomExists) {
                        val chat = ChatRoom(listOf(user1, user2), chatRoomId, chatType = chatType)
                        databaseRef.child(chatRoomId).setValue(chat)
                            .addOnSuccessListener {
                                continuation.resume(Response.Success(chatRoomId)) {}
                            }
                            .addOnFailureListener { error ->
                                continuation.resume(Response.Error("Failed to create chat room: ${error.message}")) {}
                            }
                    } else {
                        continuation.resume(Response.Error(existingChatRoomId)) {}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Response.Error("Database error: ${error.message}")) {}
                }
            })
        }
    }


    override suspend fun getUserData(userId: String): Response<Users> {
        return suspendCancellableCoroutine { continuation ->
            val database = databaseReference.database.getReference(USER_COLLECTION).child(userId)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)
                    if (user != null) {
                        continuation.resume(Response.Success(user)) { }
                    } else {
                        continuation.resume(Response.Error("User not found")) { }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Response.Error(error.message)) { }
                }
            })
        }

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


    override suspend fun openChatRoom(user1: String, user2Id: String): Response<String> {

        return suspendCancellableCoroutine { continuation ->
            val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var chatRoomExists = false
                    var existingChatRoomId = ""

                    for (chatSnapshot in snapshot.children) {
                        val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                        if (chatRoom != null && chatRoom.users!!.containsAll(
                                listOf(
                                    user1,
                                    user2Id
                                )
                            )
                        ) {
                            existingChatRoomId = chatSnapshot.key.toString()
                            chatRoomExists = true
                            break
                        }
                    }
                    if (chatRoomExists) {
                        continuation.resume(Response.Success(existingChatRoomId)) { }
                    } else {
                        continuation.resume(Response.Error("Chat room not found")) { }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Response.Error("Database error: ${error.message}")) { }
                }
            })
        }


    }

    override suspend fun createGroupChatRoom(
        currentUser: String,
        groupMembers: List<String>,
        chatId: String,
        groupName: String,
        groupImg: String
    ): Response<String> {

        return suspendCancellableCoroutine { continuation ->
            val databaseRef = databaseReference.database.getReference(CHAT_COLLECTION)

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatType: String = "group"
                    val newGroupMembers = groupMembers.toMutableList()
                    newGroupMembers.add(currentUser)


                    val chat = ChatRoom(newGroupMembers, chatId, groupName, groupImg, chatType)
                    databaseRef.child(chatId).setValue(chat)
                        .addOnSuccessListener {
                            continuation.resume(Response.Success(chatId)) {}
                        }
                        .addOnFailureListener { error ->
                            continuation.resume(Response.Error("Failed to create chat room: ${error.message}")) {}
                        }

                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Response.Error("Database error: ${error.message}")) {}
                }
            })
        }


    }


}