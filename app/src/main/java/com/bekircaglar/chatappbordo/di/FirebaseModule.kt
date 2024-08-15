package com.bekircaglar.chatappbordo.di

import com.bekircaglar.chatappbordo.data.repository.AuthRepositoryImp
import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
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
    fun provideAuthRepository(auth: FirebaseAuth,): AuthRepository{
        return  AuthRepositoryImp(auth)
    }
}