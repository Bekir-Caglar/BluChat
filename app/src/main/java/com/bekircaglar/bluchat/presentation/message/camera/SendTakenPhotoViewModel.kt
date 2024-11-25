package com.bekircaglar.bluchat.presentation.message.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.model.message.MessageType
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SendTakenPhotoViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val auth: FirebaseAuth
):ViewModel() {

    private val _currentUser = auth.currentUser!!

    fun sendMessage(imageUrl: String="",message: String, chatId: String, messageType: String) = viewModelScope.launch {

        val timestamp = System.currentTimeMillis()
        val randomId = "$timestamp-${UUID.randomUUID()}"

        if (messageType == MessageType.IMAGE.toString()){
            val imageMessage = Message(
                messageId = randomId,
                senderId = _currentUser.uid,
                message = message,
                timestamp = timestamp,
                messageType = messageType,
                imageUrl = imageUrl,
            )

            sendMessageUseCase(imageMessage, chatId).collect { response ->
                when (response) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {

                    }

                    is Response.Error -> {
                    }
                    else -> {}

                }
            }
        }
    }
}