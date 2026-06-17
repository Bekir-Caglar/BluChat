package com.bekircaglar.bluchat.data.repository.local

import com.bekircaglar.bluchat.data.local.dao.UsersDao
import com.bekircaglar.bluchat.data.local.entities.toDomainModel
import com.bekircaglar.bluchat.data.local.entities.toEntity
import com.bekircaglar.bluchat.domain.model.Users
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUsersRepository @Inject constructor(
    private val usersDao: UsersDao
) {

    suspend fun getUserById(uid: String): Users? {
        return usersDao.getUserById(uid)?.toDomainModel()
    }

    fun getUserByIdFlow(uid: String): Flow<Users?> {
        return usersDao.getUserByIdFlow(uid).map { it?.toDomainModel() }
    }

    suspend fun getAllUsers(): List<Users> {
        return usersDao.getAllUsers().map { it.toDomainModel() }
    }

    fun getAllUsersFlow(): Flow<List<Users>> {
        return usersDao.getAllUsersFlow().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun searchUsers(query: String): List<Users> {
        return usersDao.searchUsers(query).map { it.toDomainModel() }
    }

    suspend fun insertUser(user: Users) {
        usersDao.insertUser(user.toEntity())
    }

    suspend fun insertUsers(users: List<Users>) {
        usersDao.insertUsers(users.map { it.toEntity() })
    }

    suspend fun updateUser(user: Users) {
        usersDao.updateUser(user.toEntity())
    }

    suspend fun deleteUser(user: Users) {
        usersDao.deleteUser(user.toEntity())
    }

    suspend fun deleteUserById(uid: String) {
        usersDao.deleteUserById(uid)
    }

    suspend fun deleteAllUsers() {
        usersDao.deleteAllUsers()
    }

    suspend fun getUsersToSync(timestamp: Long): List<Users> {
        return usersDao.getUsersToSync(timestamp).map { it.toDomainModel() }
    }
}