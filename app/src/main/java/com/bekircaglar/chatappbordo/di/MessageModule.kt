package com.bekircaglar.chatappbordo.di

import com.bekircaglar.chatappbordo.data.repository.MessageRepositoryImp
import com.bekircaglar.chatappbordo.data.repository.ProfileRepositoryImp
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
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