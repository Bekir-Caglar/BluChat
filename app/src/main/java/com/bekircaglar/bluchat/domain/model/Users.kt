package com.bekircaglar.bluchat.domain.model

data class Users(
    val uid: String= "",
    var name: String=  "",
    var surname : String= "",
    val email: String= "",
    var phoneNumber: String= "",
    var profileImageUrl: String= "",
    var status: Boolean = false,
    var lastSeen: String = "",
    var contactsIdList: List<String> = emptyList(),
)
