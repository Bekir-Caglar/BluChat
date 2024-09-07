package com.bekircaglar.bluchat.di

import com.bekircaglar.bluchat.data.repository.ProfileRepositoryImp
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    fun provideProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository{
        return profileRepositoryImp

    }
}