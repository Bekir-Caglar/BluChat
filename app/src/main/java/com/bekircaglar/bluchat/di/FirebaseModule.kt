package com.bekircaglar.bluchat.di

import android.content.Context
import com.bekircaglar.bluchat.data.local.database.BluChatDatabase
import com.bekircaglar.bluchat.data.repository.AuthRepositoryImp
import com.bekircaglar.bluchat.data.repository.local.LocalUsersRepository
import com.bekircaglar.bluchat.data.sync.DataSyncService
import com.bekircaglar.bluchat.data.sync.SyncManager
import com.bekircaglar.bluchat.domain.repository.AuthRepository
import com.bekircaglar.bluchat.utils.network.NetworkConnectivityMonitor
import com.bekircaglar.bluchat.utils.network.NetworkUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideAuthRepository(
        auth: FirebaseAuth,
        databaseReference: DatabaseReference,
        localUsersRepository: LocalUsersRepository,
        networkUtils: NetworkUtils
    ): AuthRepository {
        return AuthRepositoryImp(auth, databaseReference, localUsersRepository, networkUtils)
    }

    @Provides
    @Singleton
    fun provideBluChatDatabase(@ApplicationContext context: Context): BluChatDatabase {
        return BluChatDatabase.getDatabase(context)
    }

    @Provides
    fun provideUsersDao(database: BluChatDatabase) = database.usersDao()

    @Provides
    fun provideChatRoomDao(database: BluChatDatabase) = database.chatRoomDao()

    @Provides
    fun provideMessageDao(database: BluChatDatabase) = database.messageDao()

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityMonitor(@ApplicationContext context: Context): NetworkConnectivityMonitor {
        return NetworkConnectivityMonitor(context)
    }
}