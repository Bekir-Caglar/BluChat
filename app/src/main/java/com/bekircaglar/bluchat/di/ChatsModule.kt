package com.bekircaglar.bluchat.di

import com.bekircaglar.bluchat.data.repository.ChatInfoRepositoryImp
import com.bekircaglar.bluchat.data.repository.ChatRepositoryImp
import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
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

    @Provides
    fun provideChatInfoRepository(chatInfoRepositoryImp: ChatInfoRepositoryImp): ChatInfoRepository {
        return chatInfoRepositoryImp
    }


}