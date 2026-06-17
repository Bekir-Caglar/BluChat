package com.bekircaglar.bluchat.data.local.dao

import androidx.room.*
import com.bekircaglar.bluchat.data.local.entities.UsersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): UsersEntity?

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserByIdFlow(uid: String): Flow<UsersEntity?>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UsersEntity>

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UsersEntity>>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR surname LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UsersEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UsersEntity>)

    @Update
    suspend fun updateUser(user: UsersEntity)

    @Delete
    suspend fun deleteUser(user: UsersEntity)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserById(uid: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM users WHERE syncedAt < :timestamp")
    suspend fun getUsersToSync(timestamp: Long): List<UsersEntity>
}