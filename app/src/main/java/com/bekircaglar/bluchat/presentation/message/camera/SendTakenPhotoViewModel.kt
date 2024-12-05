package com.bekircaglar.bluchat.presentation.message.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.model.message.MessageType
import com.bekircaglar.bluchat.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.SetLastMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.sendNotificationToChannel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SendTakenPhotoViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getUserFromChatIdUseCase: GetUserFromChatIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val setLastMessageUseCase: SetLastMessageUseCase,

    private val auth: FirebaseAuth,
):ViewModel() {

    fun sendMessage(imageUrl: String="",message: String, chatId: String, messageType: String) = viewModelScope.launch {

        val timestamp = System.currentTimeMillis()
        val randomId = "$timestamp-${UUID.randomUUID()}"
        var myMessage = message

        val messageToSend: Message = when (messageType) {

            MessageType.IMAGE.toString() -> {
                Message(
                    messageId = randomId,
                    senderId = auth.currentUser?.uid,
                    message = if (myMessage != "") message
                    else {
                        myMessage = "Image 🏞️"
                        message
                    },
                    messageType = messageType,
                    imageUrl = imageUrl,
                    timestamp = timestamp,
                )
            }

            MessageType.VIDEO.toString() -> Message(
                messageId = randomId,
                senderId = auth.currentUser?.uid,
                message = if (myMessage != "") message
                else {
                    myMessage = "Video 🎥"
                    message
                },
                videoUrl = imageUrl,
                messageType = messageType,
                timestamp = timestamp,
            )

            else -> Message(
                messageId = randomId,
                senderId = auth.currentUser?.uid,
                message = message,
                messageType = messageType,
                timestamp = timestamp,
            )



        }
        sendMessageUseCase(messageToSend, chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    setLastMessage(messageToSend, chatId)
                    sendNotification(chatId, myMessage, imageUrl)
                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }

    }

    private fun sendNotification(chatId: String, message: String, imageUrl: String?) {
        viewModelScope.launch {
            val chatUsersIdResponse = getUserFromChatIdUseCase(chatId).first()
            if (chatUsersIdResponse is Response.Success) {
                val chatUsersId = chatUsersIdResponse.data
                val currentUserId = auth.currentUser?.uid
                val currentUserResponse = currentUserId?.let { getUserUseCase.getUserData(it).first() }
                if (currentUserResponse is Response.Success) {
                    val currentUser = currentUserResponse.data

                    chatUsersId.filterNotNull().forEach { chatUserId ->
                        val userResponse = getUserUseCase.getUserData(chatUserId).first()
                        if (userResponse is Response.Success && !userResponse.data.status) {
                            sendNotificationToChannel(
                                title = "${currentUser.name} ${currentUser.surname}",
                                userId = userResponse.data.uid,
                                message = message,
                                imageUrl = imageUrl
                            )
                        }
                    }
                }
            }
        }
    }


    private fun setLastMessage(message: Message, chatId: String) = viewModelScope.launch {
        setLastMessageUseCase(chatId, message).collect {
            when (it) {
                is Response.Success -> {
                }

                is Response.Error -> {
                }

                is Response.Loading -> {
                }

                else -> {
                }
            }
        }
    }
}