package com.bekircaglar.bluchat.di

import com.bekircaglar.bluchat.data.repository.ChatInfoRepositoryImp
import com.bekircaglar.bluchat.data.repository.ChatRepositoryImp
import com.bekircaglar.bluchat.data.repository.local.LocalChatRoomRepository
import com.bekircaglar.bluchat.data.repository.local.LocalUsersRepository
import com.bekircaglar.bluchat.domain.repository.ChatInfoRepository
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import com.bekircaglar.bluchat.utils.network.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ChatsModule {

    @Provides
    fun provideChatsRepository(
        auth: FirebaseAuth,
        databaseReference: DatabaseReference,
        localUsersRepository: LocalUsersRepository,
        localChatRoomRepository: LocalChatRoomRepository,
        networkUtils: NetworkUtils
    ): ChatsRepository {
        return ChatRepositoryImp(auth, databaseReference, localUsersRepository, localChatRoomRepository, networkUtils)
    }

    @Provides
    fun provideChatInfoRepository(chatInfoRepositoryImp: ChatInfoRepositoryImp): ChatInfoRepository {
        return chatInfoRepositoryImp
    }


}