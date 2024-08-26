package com.bekircaglar.chatappbordo.di

import com.bekircaglar.chatappbordo.data.repository.ChatRepositoryImp
import com.bekircaglar.chatappbordo.data.repository.ProfileRepositoryImp
import com.bekircaglar.chatappbordo.domain.repository.ChatsRepository
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ChatsModule {

    @Provides
    fun provideChatsRepository(chatRepositoryImp: ChatRepositoryImp): ChatsRepository {
        return chatRepositoryImp

    }


}