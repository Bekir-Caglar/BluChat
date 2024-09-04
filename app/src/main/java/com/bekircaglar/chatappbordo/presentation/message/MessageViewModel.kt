package com.bekircaglar.chatappbordo.presentation.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Message
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.usecase.message.CreateMessageRoomUseCase
import com.bekircaglar.chatappbordo.domain.usecase.message.GetMessagesUseCase
import com.bekircaglar.chatappbordo.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.chatappbordo.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.GetUserUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val getMessagesUseCase: GetMessagesUseCase


) :
    ViewModel() {

    private val currentUser = auth.currentUser!!

    private val _userData = MutableStateFlow<Users?>(null)
    var userData = _userData.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages




    fun sendMessage(message: String, chatId: String) = viewModelScope.launch {

        val randomMessageId = UUID.randomUUID().toString()
        val myMessage = Message(randomMessageId,currentUser.uid, message, timestamp = System.currentTimeMillis(), false)

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
    fun getPaginatedMessages(chatId: String): Flow<PagingData<Message>> {
        return getMessagesUseCase(chatId).cachedIn(viewModelScope)
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

    fun getUserFromChatId(chatId: String) = viewModelScope.launch {

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

    private fun getUserFromUserId(userId: String) {
        viewModelScope.launch {
            val response = getUserUseCase.getUserData(userId)
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