package com.bekircaglar.bluchat.presentation.message

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.GROUP
import com.bekircaglar.bluchat.PRIVATE
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.message.CreateMessageRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.bluchat.domain.usecase.message.LoadInitialMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.LoadMoreMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.ObserveGroupStatusUseCase
import com.bekircaglar.bluchat.domain.usecase.message.ObserveUserStatusInGroupUseCase
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UploadImageUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getUserFromChatIdUseCase: GetUserFromChatIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createMessageRoomUseCase: CreateMessageRoomUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val loadInitialMessagesUseCase: LoadInitialMessagesUseCase,
    private val loadMoreMessagesUseCase: LoadMoreMessagesUseCase,
    private val getChatRoomUseCase: GetChatRoomUseCase,
    private val observeGroupStatusUseCase: ObserveGroupStatusUseCase,
    private val observeUserStatusInGroupUseCase: ObserveUserStatusInGroupUseCase,
    private val uploadImageUseCase: UploadImageUseCase


) :
    ViewModel() {

    val _currentUser = auth.currentUser!!


    private val _userData = MutableStateFlow<Users?>(null)
    var userData = _userData.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var lastKey: String? = null

    private val _selectedImageUri = MutableStateFlow<android.net.Uri?>(null)
    val selectedImageUri: StateFlow<android.net.Uri?> = _selectedImageUri

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri




    private val _isKickedOrGroupDeleted = MutableStateFlow(false)
    val isKickedOrGroupDeleted: StateFlow<Boolean> = _isKickedOrGroupDeleted

    fun observeGroupAndUserStatus(groupId: String, userId: String) {
        viewModelScope.launch {
            observeGroupStatusUseCase(groupId).collect { isGroupDeleted ->
                if (isGroupDeleted) {
                    _isKickedOrGroupDeleted.value = true
                }
            }
        }
        viewModelScope.launch {
            observeUserStatusInGroupUseCase(groupId, userId).collect { isUserKicked ->
                if (isUserKicked) {
                    _isKickedOrGroupDeleted.value = true
                }
            }
        }
    }


    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            uploadImageUseCase.invoke(uri).collect {
                when (it) {
                    is Response.Success -> {
                        _uploadedImageUri.value = it.data.toUri()


                    }

                    is Response.Error -> {
                    }

                    else -> {
                    }
                }
            }
        }

    }


    fun loadInitialMessages(chatId: String) {
        viewModelScope.launch {
            loadInitialMessagesUseCase(chatId).collect { messages ->
                _messages.value = messages
                lastKey = messages.lastOrNull()?.messageId
            }
        }
    }

    fun loadMoreMessages(chatId: String) {
        lastKey?.let {
            viewModelScope.launch {
                loadMoreMessagesUseCase(chatId, it).collect { moreMessages ->
                    _messages.value += moreMessages
                    lastKey = moreMessages.lastOrNull()?.messageId
                }
            }
        }
    }


    fun sendMessage(imageUrl: String?="",message: String, chatId: String, messageType: String) = viewModelScope.launch {

        val timestamp = System.currentTimeMillis()
        val randomId = "$timestamp-${UUID.randomUUID()}"


        val myMessage = Message(
            randomId,
            _currentUser.uid,
            message,
            timestamp,
            false,
            messageType,
            imageUrl
        )

        sendMessageUseCase(myMessage, chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {

                }

                is Response.Error -> {
                }
            }
        }
    }


    fun createMessageRoom(chatId: String) = viewModelScope.launch {
        createMessageRoomUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                }

                is Response.Error -> {
                }
            }
        }
    }

    fun getChatRoom(chatId: String) = viewModelScope.launch {
        getChatRoomUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    if (response.data.chatType == GROUP) {
                        val myGroupAsUser = Users(
                            name = response.data.chatName!!,
                            profileImageUrl = response.data.chatImage!!,
                        )
                        _userData.value = myGroupAsUser
                    } else if (response.data.chatType == PRIVATE) {
                        getUserFromChatId(chatId)
                    }
                }

                is Response.Error -> {
                }
            }
        }
    }

    private fun getUserFromChatId(chatId: String) = viewModelScope.launch {

        getUserFromChatIdUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    val userId = response.data[0]

                    println("userId: $userId")

                    getUserFromUserId(userId)

                }

                is Response.Error -> {
                }
            }
        }

    }

    fun getUserFromUserId(userId: String?) {
        viewModelScope.launch {
            getUserUseCase.getUserData(userId!!).collect { response ->
                when (response) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {
                        _userData.value = response.data
                    }

                    is Response.Error -> {
                    }
                }
            }

        }

    }

    private val userNameCache = mutableMapOf<String, String>()

    fun getUserNameFromUserId(userId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (userNameCache.containsKey(userId)) {
                onResult(userNameCache[userId]!!)
            } else {
                getUserUseCase.getUserData(userId).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            userNameCache[userId] = response.data.name
                            onResult(response.data.name)
                        }

                        is Response.Error -> {
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }

    private val userColorCache = mutableMapOf<String, Color>()

    fun getUserColor(userId: String): Color {
        return userColorCache.getOrPut(userId) {
            Color(
                red = (0..255).random() / 255f,
                green = (0..255).random() / 255f,
                blue = (0..255).random() / 255f
            )
        }
    }

}