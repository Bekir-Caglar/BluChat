package com.bekircaglar.bluchat.data.local

import com.bekircaglar.bluchat.data.local.entities.UsersEntity
import com.bekircaglar.bluchat.data.local.entities.toDomainModel
import com.bekircaglar.bluchat.data.local.entities.toEntity
import com.bekircaglar.bluchat.domain.model.Users
import org.junit.Test
import org.junit.Assert.*

class EntityConversionTest {

    @Test
    fun `test Users to UsersEntity conversion`() {
        // Given
        val user = Users(
            uid = "test-uid",
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "+1234567890",
            profileImageUrl = "https://example.com/profile.jpg",
            status = true,
            lastSeen = 1234567890L,
            contactsIdList = listOf("contact1", "contact2"),
            userCreatedAt = 1234567890L,
            userUpdatedAt = 1234567890L
        )

        // When
        val userEntity = user.toEntity()

        // Then
        assertEquals(user.uid, userEntity.uid)
        assertEquals(user.name, userEntity.name)
        assertEquals(user.surname, userEntity.surname)
        assertEquals(user.email, userEntity.email)
        assertEquals(user.phoneNumber, userEntity.phoneNumber)
        assertEquals(user.profileImageUrl, userEntity.profileImageUrl)
        assertEquals(user.status, userEntity.status)
        assertEquals(user.lastSeen, userEntity.lastSeen)
        assertEquals(user.contactsIdList, userEntity.contactsIdList)
        assertEquals(user.userCreatedAt, userEntity.userCreatedAt)
        assertEquals(user.userUpdatedAt, userEntity.userUpdatedAt)
    }

    @Test
    fun `test UsersEntity to Users conversion`() {
        // Given
        val userEntity = UsersEntity(
            uid = "test-uid",
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            phoneNumber = "+1234567890",
            profileImageUrl = "https://example.com/profile.jpg",
            status = true,
            lastSeen = 1234567890L,
            contactsIdList = listOf("contact1", "contact2"),
            userCreatedAt = 1234567890L,
            userUpdatedAt = 1234567890L,
            syncedAt = System.currentTimeMillis()
        )

        // When
        val user = userEntity.toDomainModel()

        // Then
        assertEquals(userEntity.uid, user.uid)
        assertEquals(userEntity.name, user.name)
        assertEquals(userEntity.surname, user.surname)
        assertEquals(userEntity.email, user.email)
        assertEquals(userEntity.phoneNumber, user.phoneNumber)
        assertEquals(userEntity.profileImageUrl, user.profileImageUrl)
        assertEquals(userEntity.status, user.status)
        assertEquals(userEntity.lastSeen, user.lastSeen)
        assertEquals(userEntity.contactsIdList, user.contactsIdList)
        assertEquals(userEntity.userCreatedAt, user.userCreatedAt)
        assertEquals(userEntity.userUpdatedAt, user.userUpdatedAt)
    }

    @Test
    fun `test round trip conversion preserves data`() {
        // Given
        val originalUser = Users(
            uid = "test-uid",
            name = "Jane",
            surname = "Smith",
            email = "jane.smith@example.com",
            phoneNumber = "+0987654321",
            profileImageUrl = "https://example.com/jane.jpg",
            status = false,
            lastSeen = 9876543210L,
            contactsIdList = listOf("friend1", "friend2", "friend3"),
            userCreatedAt = 9876543210L,
            userUpdatedAt = 9876543210L
        )

        // When
        val convertedUser = originalUser.toEntity().toDomainModel()

        // Then
        assertEquals(originalUser.uid, convertedUser.uid)
        assertEquals(originalUser.name, convertedUser.name)
        assertEquals(originalUser.surname, convertedUser.surname)
        assertEquals(originalUser.email, convertedUser.email)
        assertEquals(originalUser.phoneNumber, convertedUser.phoneNumber)
        assertEquals(originalUser.profileImageUrl, convertedUser.profileImageUrl)
        assertEquals(originalUser.status, convertedUser.status)
        assertEquals(originalUser.lastSeen, convertedUser.lastSeen)
        assertEquals(originalUser.contactsIdList, convertedUser.contactsIdList)
        assertEquals(originalUser.userCreatedAt, convertedUser.userCreatedAt)
        assertEquals(originalUser.userUpdatedAt, convertedUser.userUpdatedAt)
    }
}