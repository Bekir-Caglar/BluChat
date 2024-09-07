package com.bekircaglar.bluchat.di

import com.bekircaglar.bluchat.data.repository.MessageRepositoryImp
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MessageModule {
    @Provides
    fun provideMessageRepository(messageRepositoryImp: MessageRepositoryImp): MessageRepository {
        return messageRepositoryImp
    }
}