# BluChat Local Database Implementation

This document describes the Room local database implementation that provides offline functionality with Firebase synchronization.

## Overview

The local database implementation adds offline capabilities to BluChat by:
- Caching data locally using Room database
- Providing offline-first data access
- Automatically syncing with Firebase when internet connection is restored
- Showing network status to users

## Architecture

### Data Layer Structure

```
data/
├── local/
│   ├── entities/          # Room entities
│   ├── dao/              # Data Access Objects
│   ├── database/         # Room database and converters
│   └── repository/       # Local repository implementations
├── repository/           # Updated repository implementations
└── sync/                # Synchronization services
```

### Key Components

#### 1. Room Entities
- **UsersEntity**: Local storage for user data
- **ChatRoomEntity**: Local storage for chat room data  
- **MessageEntity**: Local storage for message data

#### 2. DAOs (Data Access Objects)
- **UsersDao**: CRUD operations for users
- **ChatRoomDao**: CRUD operations for chat rooms
- **MessageDao**: CRUD operations for messages

#### 3. Local Repositories
- **LocalUsersRepository**: Local user data operations
- **LocalChatRoomRepository**: Local chat room operations

#### 4. Sync Services
- **DataSyncService**: Handles data synchronization between local and remote
- **SyncManager**: Manages automatic sync when network is restored
- **NetworkConnectivityMonitor**: Monitors network state changes

## How It Works

### 1. Offline-First Strategy
```kotlin
// Repository method example
override suspend fun getUsersChatList(): Flow<Response<List<ChatRoom>>> = callbackFlow {
    // 1. First, emit cached data if available
    val cachedChats = localChatRoomRepository.getChatRoomsForUser(currentUser)
    if (cachedChats.isNotEmpty()) {
        trySend(Response.Success(cachedChats))
    }
    
    // 2. If online, fetch from Firebase and update cache
    if (networkUtils.isInternetAvailable()) {
        // Fetch from Firebase and cache results
    }
}
```

### 2. Automatic Synchronization
- Network connectivity is monitored in real-time
- When device reconnects to internet, automatic sync is triggered
- Data is seamlessly updated in the background

### 3. User Experience
- Users see cached data immediately when offline
- Network status indicator shows current connectivity state
- Smooth transition between offline and online modes

## Usage

### 1. Access Cached Data
The repository automatically serves cached data when offline:

```kotlin
// This will work offline using cached data
chatRepository.getUsersChatList().collect { response ->
    when (response) {
        is Response.Success -> {
            // Display chat list (may be cached data)
        }
    }
}
```

### 2. Manual Sync
Trigger manual synchronization:

```kotlin
@Inject
lateinit var syncManager: SyncManager

// Trigger manual sync
syncManager.triggerManualSync()
```

### 3. Network Status UI
Add network status indicator to your screens:

```kotlin
@Composable
fun ChatScreen() {
    Column {
        NetworkStatusBar() // Shows online/offline status
        
        // Your chat content
    }
}
```

## Database Schema

### Users Table
- uid (PRIMARY KEY)
- name, surname, email, phoneNumber
- profileImageUrl, status, lastSeen
- contactsIdList (JSON)
- userCreatedAt, userUpdatedAt
- syncedAt (for sync tracking)

### Chat Rooms Table  
- chatId (PRIMARY KEY)
- users (JSON array)
- chatName, chatImage, chatType
- chatAdminId, chatLastMessage, chatLastMessageTime
- chatCreatedAt, chatUpdatedAt
- syncedAt (for sync tracking)

### Messages Table
- messageId (PRIMARY KEY)
- chatId (foreign key)
- senderId, message, timestamp
- read, messageType, edited, pinned, deleted, starred
- media URLs, location data
- syncedAt (for sync tracking)

## Benefits

1. **Offline Functionality**: Users can view cached chats and messages when offline
2. **Better Performance**: Local data access is faster than network requests
3. **Reduced Data Usage**: Minimizes redundant network requests
4. **Improved UX**: Immediate data loading from cache
5. **Automatic Sync**: Seamless data updates when connection is restored

## Implementation Details

### Dependencies Added
```kotlin
// Room database
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)

// JSON serialization for type converters
implementation(libs.gson)
```

### Permissions Required
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Future Enhancements

1. **Message Caching**: Extend to cache individual messages
2. **Conflict Resolution**: Handle data conflicts when syncing
3. **Selective Sync**: Allow users to choose what to sync
4. **Background Sync**: Periodic background synchronization
5. **Data Compression**: Compress cached data to save storage

## Testing

The implementation includes unit tests for:
- Entity conversion functions
- Local repository operations
- Network connectivity detection

Run tests with:
```bash
./gradlew test
```

## Troubleshooting

### Common Issues

1. **Build Errors**: Ensure all dependencies are properly added
2. **Sync Not Working**: Check network permissions in manifest
3. **Data Not Cached**: Verify repository injection is correct
4. **UI Not Updating**: Make sure Flow collection is properly set up

### Debug Logging

Enable debug logging to monitor sync operations:
```kotlin
// Look for these log tags:
// - "DataSyncService"
// - "SyncManager" 
// - "NetworkMonitor"
```

This implementation provides a solid foundation for offline functionality while maintaining the existing Firebase-based architecture.