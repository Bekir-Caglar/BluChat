package com.bekircaglar.bluchat.presentation.message

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.message.CreateMessageRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.bluchat.domain.usecase.message.LoadInitialMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.LoadMoreMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
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
    private val getChatRoomUseCase: GetChatRoomUseCase


) :
    ViewModel() {

    val _currentUser = auth.currentUser!!


    private val _userData = MutableStateFlow<Users?>(null)
    var userData = _userData.asStateFlow()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _userNameFromUserId = MutableStateFlow<String>("")
    val userNameFromUserId = _userNameFromUserId.asStateFlow()

    private var lastKey: String? = null

    fun loadInitialMessages(chatId: String) {
        loadInitialMessagesUseCase(chatId).onEach { messages ->
            _messages.value = messages
            lastKey = messages.lastOrNull()?.messageId
        }.launchIn(viewModelScope)
    }

    fun loadMoreMessages(chatId: String) {
        lastKey?.let {
            loadMoreMessagesUseCase(chatId, it).onEach { moreMessages ->
                _messages.value += moreMessages
                lastKey = moreMessages.lastOrNull()?.messageId
            }.launchIn(viewModelScope)
        }
    }


    fun sendMessage(message: String, chatId: String) = viewModelScope.launch {

        val timestamp = System.currentTimeMillis()
        val randomId = "$timestamp-${UUID.randomUUID()}"


        val myMessage = Message(randomId, _currentUser.uid, message, timestamp = timestamp, false)

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
                    if (response.data.chatType == "group") {
                        val myGroupAsUser = Users(
                            name = response.data.chatName!!,
                            profileImageUrl = response.data.chatImage!!,
                        )
                        _userData.value = myGroupAsUser
                    }
                    else if (response.data.chatType == "private") {
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
                    val userId = response.data

                    getUserFromUserId(userId)

                }

                is Response.Error -> {
                }
            }
        }

    }

    fun getUserFromUserId(userId: String) {
        viewModelScope.launch {
            when (val response = getUserUseCase.getUserData(userId)) {
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
    private val userNameCache = mutableMapOf<String, String>()

    fun getUserNameFromUserId(userId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (userNameCache.containsKey(userId)) {
                onResult(userNameCache[userId]!!)
            } else {
                when (val response = getUserUseCase.getUserData(userId)) {
                    is Response.Loading -> {
                    }
                    is Response.Success -> {
                        val userName = response.data.name
                        userNameCache[userId] = userName
                        onResult(userName)
                    }
                    is Response.Error -> {
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