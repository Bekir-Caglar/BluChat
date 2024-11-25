package com.bekircaglar.bluchat.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class Users(
    val uid: String= "",
    var name: String= "",
    var surname : String= "",
    val email: String= "",
    var phoneNumber: String= "",
    var profileImageUrl: String= "",
    var status: Boolean = false,
    var lastSeen: String = "",
    var contactsIdList: List<String> = emptyList(),
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        if (name.isNotBlank()) result["name"] = name
        if (surname.isNotBlank()) result["surname"] = surname
        if (email.isNotBlank()) result["email"] = email
        if (phoneNumber.isNotBlank()) result["phoneNumber"] = phoneNumber
        if (profileImageUrl.isNotBlank()) result["profileImageUrl"] = profileImageUrl
        if (status != null) result["status"] = status
        if (lastSeen.isNotBlank()) result["lastSeen"] = lastSeen
        if (contactsIdList.isNotEmpty()) result["contactsIdList"] = contactsIdList
        return result
    }
    @get:Exclude
    val fullName: String
        get() = "$name $surname"

    @get:Exclude
    val isOnline: Boolean
        get() = status == true

    @get:Exclude
    val isOffline: Boolean
        get() = status == false

    @get:Exclude
    val hasName: Boolean
        get() = !name.isNullOrBlank()

    @get:Exclude

    val hasSurname: Boolean
        get() = !surname.isNullOrBlank()


    @get:Exclude
    val hasEmail: Boolean
        get() = !email.isNullOrBlank()

    @get:Exclude

    val hasPhoneNumber: Boolean
        get() = !phoneNumber.isNullOrBlank()

    @get:Exclude


    val hasProfileImageUrl: Boolean
        get() = !profileImageUrl.isNullOrBlank()

    @get:Exclude

    val hasStatus: Boolean
        get() = status != null

    @get:Exclude

    val hasLastSeen: Boolean
        get() = !lastSeen.isNullOrBlank()

    @get:Exclude

    val hasContactsIdList: Boolean
        get() = contactsIdList.isNotEmpty()

}
