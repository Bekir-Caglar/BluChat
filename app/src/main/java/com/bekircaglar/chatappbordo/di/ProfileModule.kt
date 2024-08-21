package com.bekircaglar.chatappbordo.di

import com.bekircaglar.chatappbordo.data.repository.ProfileRepositoryImp
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    fun provideProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository{
        return profileRepositoryImp

    }


}