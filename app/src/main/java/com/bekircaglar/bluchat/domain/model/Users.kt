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
    var lastSeen: Long = 0L,
    var contactsIdList: List<String> = emptyList(),
    val userCreatedAt: Long = System.currentTimeMillis(),
    val userUpdatedAt: Long = 0L
){

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
    val hasContactsIdList: Boolean
        get() = contactsIdList.isNotEmpty()

}
