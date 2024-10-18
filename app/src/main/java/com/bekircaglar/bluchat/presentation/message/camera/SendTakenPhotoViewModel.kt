package com.bekircaglar.bluchat.presentation.message.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.navigation.Screens
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
                else -> {}

            }
        }
    }
}