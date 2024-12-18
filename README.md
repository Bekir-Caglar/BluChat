# 📱 **BluChat**  
![Project Banner](banner.png)  

---

### 🔖 **About the Project**  
BluChat is a user-friendly chat application that enables users to register, manage their profiles, and customize application settings. Designed with modern architectural principles (Clean Architecture, MVVM), it is developed using Jetpack Compose to provide a seamless and interactive messaging experience.

---

### 🎥 **App Demo Video**  
Experience BluChat in action by watching the demo video below:  
[![BluChat Demo](BluChatVideo.mp4)
*Click the thumbnail to watch the video on YouTube.*

---

### 🖥 **Screenshots**  

| **Screen**                  | **Light Mode**                          | **Dark Mode**                          | **Description**                                                                                                     |
| --------------------------- | --------------------------------------- | --------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| **Chat Screen**             | ![Chat Screen](ChatsW.jpeg)            | ![Chat Screen](ChatsD.jpeg)            | The main screen displaying a list of all active conversations. Users can view recent messages and initiate chats.   |
| **Profile Screen**          | ![Profile Screen](ProfileW.jpeg)       | ![Profile Screen](ProfileD.jpeg)       | This screen allows users to view and update their profile details, including profile pictures, name, and status.    |
| **Contacts Screen**         | ![Contacts Screen](ContactsW.jpeg)     | ![Contacts Screen](ContactsD.jpeg)     | A directory of all contacts in the application. Users can search for, add, or block contacts from this section.     |
| **Message Screen**          | ![Message Screen](MessageW.jpeg)       | ![Message Screen](MessageD.jpeg)       | Displays the chat interface for a single conversation. Users can send messages, emojis, and share media files here. |
| **User Info Screen**        | ![User Info Screen](UserInfoW.jpeg)    | ![User Info Screen](UserInfoD.jpeg)    | Shows detailed information about a user, such as their full name, contact details, and profile picture.             |
| **Starred Messages Screen** | ![Starred Messages Screen](StarredW.jpeg) | ![Starred Messages Screen](StarredD.jpeg) | A screen dedicated to organizing and viewing all important (starred) messages for quick access.                     |

---

### 🚀 **Features**  

| **Feature**                | **Description**                                                                              
| --------------------------- | ---------------------------------------------------------------------------------------------
| **User Authentication**    | Securely log in and register using Firebase Authentication. Supports email/password, Google and Facebook.       |
| **Chat with Contacts**      | Easily start conversations with your contacts. Includes real-time message synchronization.                          |
| **Create Group Chats**      | Create group conversations to stay connected with multiple people at once. Supports adding/removing participants.    |
| **Notifications**      | Get real-time alerts for new messages and updates, keeping you engaged and informed even when you're not using the app. |
| **Send Text Messages**      | Send plain text messages seamlessly in real-time with rich formatting options.                                       |
| **Send Audio Messages**     | Record and send voice messages to your contacts for faster communication.                                           |
| **Send Video Messages**     | Share videos directly within the chat, with support for preview and playback.                                       |
| **Send Image Messages**     | Upload and share images in conversations, with thumbnail previews and full-screen viewing options.                  |
| **Send Location Messages**  | Share your current location with others in a chat. Integrated with Google Maps for easy navigation.                 |
| **Take Photo with Camera**  | Capture moments instantly with your camera and share directly in the chat.                                          |
| **Search Phone Number in Realtime Database** | Quickly find and connect with users by searching their phone numbers stored in the Firebase Realtime Database. |
| **Pagination on Messages Screen** | Efficiently load older messages in chunks, improving performance and user experience for large chat histories.    |
| **Profile Update**          | Update your profile picture, name, and status directly within the app to keep your profile up-to-date.              |
| **Star, Delete, Edit, and Pin Messages** | Organize your chats better by starring important messages, editing sent ones, pinning crucial conversations, or deleting unwanted content. |
| **Set Dark Mode**           | Customize the app appearance by toggling between light and dark themes.                                             |

### 🛠 **Technologies Used**  

| Technology            | Description                                |
| ---------------------- | ------------------------------------------ |
| **Kotlin**            | Main programming language for the project. |
| **Jetpack Compose**   | Modern UI toolkit for building native Android UIs. |
| **Firebase**          | Used for user authentication, real-time database, and cloud storage. |
| **Clean Architecture**| Implements a modular and maintainable structure with clear separation of concerns. |
| **Dagger Hilt**       | Framework for dependency injection to enhance scalability and testability. |
| **Paging**            | Efficiently handles large datasets and supports infinite scrolling. |
| **Coil**              | Image loading library optimized for Compose. |
| **OneSignal**         | Push notification service integration. |

### 📂 **Folder Structure**  

Project folder structure:

```plaintext
📂 BluChat  
📂  data  
├── 📂  paging           #  Paging
└── 📂  repository        # Repository implementations  
📂  domain  
├── 📂  model             # Model classes  
├── 📂  repository        # Interfaces  
└── 📂  use_case          # UseCase classes  
📂  presentation  
├── 📂  screens           # Screens' UI and ViewModels  
├── 📂  components        # Reusable UI components  
└── 📂  navigation        # Navigation graph setup  
📂  utils                 # Helper files (extensions, constants, etc.)  
└── 📄 build.gradle          # Gradle dependencies
