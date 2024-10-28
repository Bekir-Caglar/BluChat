package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ContactsRepository
import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.USER_COLLECTION
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactsRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference,
) : ContactsRepository {
    override suspend fun getContacts(userId: String): Flow<Response<List<String?>>> = callbackFlow {

        val dbRef = databaseReference.child(USER_COLLECTION).child(userId).child("contactsIdList")

        val listener = dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactsList = snapshot.children.map { it.getValue(String::class.java) }
                trySend(Response.Success(contactsList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error(error.message))
            }
        })

        awaitClose()
    }

    override suspend fun addContact(phoneNumber: String, userId: String): Flow<Response<Boolean>> =
        callbackFlow {
            val userRef = databaseReference.child(USER_COLLECTION)

            try {
                userRef.get().addOnSuccessListener { snapshot ->
                    var matchedUserId: String? = null
                    for (childSnapshot in snapshot.children) {
                        val userPhone =
                            childSnapshot.child("phoneNumber").getValue(String::class.java)
                        if (userPhone == phoneNumber) {
                            matchedUserId = childSnapshot.key
                            break
                        }
                    }

                    if (matchedUserId != null) {
                        val dbRef = userRef.child(userId).child("contactsIdList")
                        dbRef.get().addOnSuccessListener {
                            if (it.exists()) {
                                val contactsSet =
                                    it.children.mapNotNull { it.getValue(String::class.java) }
                                        .toMutableSet()
                                contactsSet.add(matchedUserId)
                                dbRef.setValue(contactsSet.toList()).addOnSuccessListener {
                                    trySend(Response.Success(true))
                                }.addOnFailureListener { error ->
                                    trySend(Response.Error(error.message.toString()))
                                }
                            } else {
                                dbRef.setValue(listOf(matchedUserId)).addOnSuccessListener {
                                    trySend(Response.Success(true))
                                }.addOnFailureListener { error ->
                                    trySend(Response.Error(error.message.toString()))
                                }
                            }
                        }
                    } else {
                        trySend(Response.Error("Telefon numarasına sahip kullanıcı bulunamadı"))
                    }
                }.addOnFailureListener { error ->
                    trySend(Response.Error(error.message.toString()))
                }
            } catch (e: Exception) {
                trySend(Response.Error(e.message.toString()))
            }
            awaitClose()
        }


    override suspend fun getAppUserContacts(
        contacts: List<Users>,
        userId: String
    ): Flow<Response<List<Users>>> = callbackFlow {
        val dbRef = databaseReference.child(USER_COLLECTION).child(userId).child("contactsIdList")
        val userRef = databaseReference.child(USER_COLLECTION)

        val listener = dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(contactIdListSnapshot: DataSnapshot) {
                val contactIdList =
                    contactIdListSnapshot.children.mapNotNull { it.getValue(String::class.java) }

                userRef.get().addOnSuccessListener { allUsersSnapshot ->
                    val matchedUsers = mutableListOf<Users>()

                    for (contactId in contactIdList) {
                        val userSnapshot = allUsersSnapshot.child(contactId)
                        val user = userSnapshot.getValue(Users::class.java)
                        if (user != null) {
                            matchedUsers.add(user)
                        }
                    }

                    for (userSnapshot in allUsersSnapshot.children) {
                        val user = userSnapshot.getValue(Users::class.java)

                        if (user != null && contacts.any { it.phoneNumber == user.phoneNumber || it.phoneNumber == "+90${user.phoneNumber}" }) {
                            matchedUsers.add(user)
                        }
                    }

                    trySend(Response.Success(matchedUsers.distinctBy { it.uid }))
                }.addOnFailureListener { error ->
                    trySend(Response.Error(error.message.toString()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error(error.message))
            }
        })

        awaitClose { dbRef.removeEventListener(listener) }
    }


}