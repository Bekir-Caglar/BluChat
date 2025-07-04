# BluChat Database Information

## Database File: bluchat_database.db

This is a pre-populated SQLite database file that contains the schema for the BluChat application's local Room database.

### Tables

1. **users** - Stores user information
2. **chat_rooms** - Stores chat room data
3. **messages** - Stores chat messages

### Features

- **Pre-created schema**: The database comes with all tables and indexes already created
- **Performance optimized**: Includes indexes for common queries
- **Room compatible**: Configured to work seamlessly with Android Room database
- **Firebase sync ready**: Designed to work with Firebase synchronization

### Usage

The database is automatically copied from assets to the app's internal storage when the app is first installed. This provides:

- Faster initial app startup
- Immediate availability of database structure
- Better offline experience from the start

### Synchronization

When the app has internet connectivity, data will be synchronized with Firebase Realtime Database, and the local database will be updated accordingly.