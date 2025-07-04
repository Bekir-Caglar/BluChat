package com.bekircaglar.bluchat.data.sync

import android.content.Context
import android.util.Log
import com.bekircaglar.bluchat.data.repository.local.LocalChatRoomRepository
import com.bekircaglar.bluchat.data.repository.local.LocalUsersRepository
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.USER_COLLECTION
import com.bekircaglar.bluchat.utils.network.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSyncService @Inject constructor(
    private val context: Context,
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val localUsersRepository: LocalUsersRepository,
    private val localChatRoomRepository: LocalChatRoomRepository,
    private val networkUtils: NetworkUtils
) {
    
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val tag = "DataSyncService"
    
    /**
     * Performs a full sync of all data when internet connection is restored
     */
    fun syncAllData() {
        if (!networkUtils.isInternetAvailable()) {
            Log.d(tag, "No internet connection, skipping sync")
            return
        }
        
        syncScope.launch {
            try {
                syncUsers()
                syncChatRooms()
                Log.d(tag, "Full data sync completed successfully")
            } catch (e: Exception) {
                Log.e(tag, "Error during full sync: ${e.message}")
            }
        }
    }
    
    /**
     * Syncs user data from Firebase to local database
     */
    private suspend fun syncUsers() {
        try {
            val usersRef = databaseReference.child(USER_COLLECTION)
            val snapshot = usersRef.get().await()
            
            val users = mutableListOf<Users>()
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(Users::class.java)
                if (user != null) {
                    users.add(user)
                }
            }
            
            if (users.isNotEmpty()) {
                localUsersRepository.insertUsers(users)
                Log.d(tag, "Synced ${users.size} users to local database")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error syncing users: ${e.message}")
        }
    }
    
    /**
     * Syncs chat room data from Firebase to local database
     */
    private suspend fun syncChatRooms() {
        try {
            val currentUser = auth.currentUser?.uid
            if (currentUser == null) {
                Log.d(tag, "No authenticated user, skipping chat rooms sync")
                return
            }
            
            val chatRoomsRef = databaseReference.child(CHAT_COLLECTION)
            val snapshot = chatRoomsRef.get().await()
            
            val chatRooms = mutableListOf<ChatRoom>()
            for (chatSnapshot in snapshot.children) {
                val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                if (chatRoom != null && chatRoom.users?.contains(currentUser) == true) {
                    chatRooms.add(chatRoom)
                }
            }
            
            if (chatRooms.isNotEmpty()) {
                localChatRoomRepository.insertChatRooms(chatRooms)
                Log.d(tag, "Synced ${chatRooms.size} chat rooms to local database")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error syncing chat rooms: ${e.message}")
        }
    }
    
    /**
     * Syncs a specific user's data
     */
    fun syncUser(userId: String) {
        if (!networkUtils.isInternetAvailable()) return
        
        syncScope.launch {
            try {
                val userRef = databaseReference.child(USER_COLLECTION).child(userId)
                val snapshot = userRef.get().await()
                val user = snapshot.getValue(Users::class.java)
                
                if (user != null) {
                    localUsersRepository.insertUser(user)
                    Log.d(tag, "Synced user: ${user.uid}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error syncing user $userId: ${e.message}")
            }
        }
    }
    
    /**
     * Syncs a specific chat room's data
     */
    fun syncChatRoom(chatId: String) {
        if (!networkUtils.isInternetAvailable()) return
        
        syncScope.launch {
            try {
                val chatRef = databaseReference.child(CHAT_COLLECTION).child(chatId)
                val snapshot = chatRef.get().await()
                val chatRoom = snapshot.getValue(ChatRoom::class.java)
                
                if (chatRoom != null) {
                    localChatRoomRepository.insertChatRoom(chatRoom)
                    Log.d(tag, "Synced chat room: ${chatRoom.chatId}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error syncing chat room $chatId: ${e.message}")
            }
        }
    }
    
    /**
     * Clears all local data - useful for logout or data corruption
     */
    fun clearAllLocalData() {
        syncScope.launch {
            try {
                localUsersRepository.deleteAllUsers()
                localChatRoomRepository.deleteAllChatRooms()
                Log.d(tag, "Cleared all local data")
            } catch (e: Exception) {
                Log.e(tag, "Error clearing local data: ${e.message}")
            }
        }
    }
}