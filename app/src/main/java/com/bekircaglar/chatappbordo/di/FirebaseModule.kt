package com.bekircaglar.chatappbordo.di

import androidx.compose.ui.input.key.Key.Companion.D
import com.bekircaglar.chatappbordo.data.repository.AuthRepositoryImp
import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseDatabaseInstance() = Firebase.database.reference

    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth,databaseReference: DatabaseReference): AuthRepository {
        return AuthRepositoryImp(auth,databaseReference)
    }
}