package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.USER_COLLECTION
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private var databaseReference: DatabaseReference
) : AuthRepository {

    override fun isUserAuthenticatedInFirebase(): Response<String> {
        return Response.Success(auth.currentUser?.uid ?: "")
    }

    override suspend fun signIn(email: String, password: String): Response<String> {
        auth.signInWithEmailAndPassword(email, password).await()
        try {
            if (auth.currentUser != null) {
                return Response.Success(auth.currentUser.toString())
            } else {
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }

    }

    override suspend fun signUp(email: String, password: String): Response<String> {
        auth.createUserWithEmailAndPassword(email, password).await()

        try {
            if (auth.currentUser != null) {
                return Response.Success(auth.currentUser.toString())
            } else {
                return Response.Error("Unknown Error")
            }
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }


    override suspend fun createUser(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        userImageUrl: String?
    ): Response<String> {
        try {
            val user = Users(
                name = name,
                surname = surname,
                phoneNumber = phoneNumber,
                email = email,
                uid = auth.currentUser?.uid.toString(),
                profileImageUrl = if (userImageUrl.isNullOrEmpty()) "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/def_user.png?alt=media&token=54d55dc5-4fad-415a-8b6f-d0f3b0619f31"
                else userImageUrl,
                status = false,
                lastSeen = 0L,
                contactsIdList = emptyList(),
                userCreatedAt = System.currentTimeMillis(),
            )
            databaseReference.child(USER_COLLECTION).child(auth.currentUser?.uid.toString()).setValue(user)
                .await()
            return Response.Success("User Created")
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }
    }

    override suspend fun checkPhoneNumber(phoneNumber: String): Flow<Response<String>> = callbackFlow {
        val database = databaseReference.database.getReference(USER_COLLECTION)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var phoneExists = false
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    if (user?.phoneNumber.toString() == phoneNumber) {
                        phoneExists = true
                        break
                    }
                }
                if (phoneExists) {
                    trySend(Response.Error("Phone Number Exists")).isSuccess
                } else {
                    trySend(Response.Success("Phone Number Does Not Exist")).isSuccess
                }
                close()
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Error(error.message)).isSuccess
                close(error.toException())
            }
        }

        database.addListenerForSingleValueEvent(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    override suspend fun checkIsUserAlreadyExist(email: String): Flow<Response<Boolean>> = callbackFlow {

        val database = databaseReference.database.getReference(USER_COLLECTION)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userExists = false
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    if (user?.email.toString() == email) {
                        userExists = true
                        break
                    }
                }
                if (userExists){
                    trySend(Response.Success(true)).isSuccess
                }else{
                    trySend(Response.Success(false)).isSuccess
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        database.addListenerForSingleValueEvent(listener)
        awaitClose { database.removeEventListener(listener) }
    }

}