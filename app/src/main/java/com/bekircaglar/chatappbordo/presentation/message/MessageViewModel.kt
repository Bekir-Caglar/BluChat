package com.bekircaglar.chatappbordo.presentation.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.usecase.chats.CreateChatRoomUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.GetUserUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val createChatRoomUseCase: CreateChatRoomUseCase,private val auth: FirebaseAuth,private val getUserUseCase: GetUserUseCase) :
    ViewModel() {

    private val _userData = MutableStateFlow<Users?>(null)
    var userData = _userData.asStateFlow()

    val currentUserId = auth.currentUser?.uid.toString()

    fun createChatRoom(user: String, chatRoomId: String) = viewModelScope.launch {
        createChatRoomUseCase.invoke(currentUserId, user, chatRoomId)
        getUserData(user)
    }
    private fun getUserData(user: String) = viewModelScope.launch {

        when(val response = getUserUseCase.getUserData(user)){
            is Response.Success -> {
                _userData.value = response.data
            }
            is Response.Error -> {
                //Handle Error
            }
            else -> {

            }
        }
    }


}