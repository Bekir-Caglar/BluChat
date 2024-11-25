package com.bekircaglar.bluchat.presentation.message.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.BuildConfig
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.model.message.MessageType
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.utils.IMAGE
import com.bekircaglar.bluchat.utils.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sendMessageUseCase: SendMessageUseCase

):ViewModel() {

    fun sendLocation(latitude: Double?, longitude: Double?,chatId:String,onResult:()-> Unit)= viewModelScope.launch{
        val timestamp = System.currentTimeMillis()
        val randomId = "$timestamp-${UUID.randomUUID()}"
        val mapsApiKey = BuildConfig.GOOGLE_MAPS_KEY

        val locationMessage = Message(
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            locationName = "",
            messageType = MessageType.LOCATION.toString(),
            timestamp = timestamp,
            messageId = randomId,
            senderId = auth.currentUser?.uid,

        )


        sendMessageUseCase.invoke(message = locationMessage, chatId = chatId).collect{
            when(it){
                is Response.Success -> {
                    onResult()
                }
                else -> {}
            }
        }

    }

}