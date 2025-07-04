package com.bekircaglar.bluchat.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bekircaglar.bluchat.data.local.database.Converters
import com.bekircaglar.bluchat.domain.model.Users

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class UsersEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNumber: String,
    val profileImageUrl: String,
    val status: Boolean,
    val lastSeen: Long,
    val contactsIdList: List<String>,
    val userCreatedAt: Long,
    val userUpdatedAt: Long,
    val syncedAt: Long = System.currentTimeMillis()
)

// Extension functions to convert between domain model and entity
fun UsersEntity.toDomainModel(): Users {
    return Users(
        uid = uid,
        name = name,
        surname = surname,
        email = email,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        status = status,
        lastSeen = lastSeen,
        contactsIdList = contactsIdList,
        userCreatedAt = userCreatedAt,
        userUpdatedAt = userUpdatedAt
    )
}

fun Users.toEntity(): UsersEntity {
    return UsersEntity(
        uid = uid,
        name = name,
        surname = surname,
        email = email,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        status = status,
        lastSeen = lastSeen,
        contactsIdList = contactsIdList,
        userCreatedAt = userCreatedAt,
        userUpdatedAt = userUpdatedAt
    )
}