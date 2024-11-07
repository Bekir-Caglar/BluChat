package com.bekircaglar.bluchat.presentation.message.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.BuildConfig
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.repository.AuthRepository
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

        val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=400x400&key=$mapsApiKey"

        val myMessage = Message(
            messageId = randomId,
            senderId = auth.currentUser!!.uid,
            message = "",
            timestamp = timestamp,
            read = false,
            messageType = IMAGE,
            imageUrl = mapUrl,
            replyTo = ""
        )
        sendMessageUseCase.invoke(message = myMessage, chatId = chatId).collect{
            when(it){
                is Response.Success -> {
                    onResult()
                }
                else -> {}
            }
        }

    }

}