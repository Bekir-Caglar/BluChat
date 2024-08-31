package com.bekircaglar.chatappbordo.presentation.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.usecase.message.CreateMessageRoomUseCase
import com.bekircaglar.chatappbordo.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val getUserFromChatIdUseCase: GetUserFromChatIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createMessageRoomUseCase: CreateMessageRoomUseCase
) :
    ViewModel() {

    private val _userData = MutableStateFlow<Users?>(null)
    var userData = _userData.asStateFlow()





    init {

    }

    fun sendMessage(message:String)=viewModelScope.launch {


    }


    fun createMessageRoom(chatId: String) = viewModelScope.launch {
        createMessageRoomUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }
                is Response.Success -> {
                    println(response.data)
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

    private fun getUserFromUserId(userId:String){
        viewModelScope.launch {
            val response = getUserUseCase.getUserData(userId)
            when(response){
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