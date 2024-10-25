package com.bekircaglar.bluchat.di

import com.bekircaglar.bluchat.data.repository.ContactsRepositoryImp
import com.bekircaglar.bluchat.data.repository.MessageRepositoryImp
import com.bekircaglar.bluchat.domain.repository.ContactsRepository
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContactModule {
    @Provides
    fun provideContactRepository(contactsRepositoryImp: ContactsRepositoryImp): ContactsRepository {
        return contactsRepositoryImp
    }
}