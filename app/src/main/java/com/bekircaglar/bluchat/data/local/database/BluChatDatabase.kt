package com.bekircaglar.bluchat.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.bekircaglar.bluchat.data.local.dao.ChatRoomDao
import com.bekircaglar.bluchat.data.local.dao.MessageDao
import com.bekircaglar.bluchat.data.local.dao.UsersDao
import com.bekircaglar.bluchat.data.local.entities.ChatRoomEntity
import com.bekircaglar.bluchat.data.local.entities.MessageEntity
import com.bekircaglar.bluchat.data.local.entities.UsersEntity

@Database(
    entities = [
        UsersEntity::class,
        ChatRoomEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BluChatDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: BluChatDatabase? = null

        fun getDatabase(context: Context): BluChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BluChatDatabase::class.java,
                    "bluchat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}