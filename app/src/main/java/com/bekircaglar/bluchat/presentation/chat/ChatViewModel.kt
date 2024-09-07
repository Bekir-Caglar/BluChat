package com.bekircaglar.bluchat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.chats.CreateChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.GetUserChatListUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.OpenChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.SearchPhoneNumberUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val searchPhoneNumberUseCase: SearchPhoneNumberUseCase,
    private val getUserChatListUseCase: GetUserChatListUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val openChatRoomUseCase: OpenChatRoomUseCase,
    private val auth: FirebaseAuth,
):ViewModel () {


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Users>>(emptyList())
    val searchResults: StateFlow<List<Users>> = _searchResults.asStateFlow()

    private val _chatUserList = MutableStateFlow<List<Chats>>(emptyList())
    val chatUserList = _chatUserList.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _succes = MutableStateFlow<String?>(null)
    val succes: StateFlow<String?> get() = _succes

    private val currentUserId = auth.currentUser?.uid.toString()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { it.isNotEmpty() }
                .collect { query ->
                    when (val result = searchPhoneNumberUseCase(query)) {
                        is Response.Success -> {
                            _searchResults.value = result.data.let {
                                it.filter { user -> user.uid != auth.currentUser?.uid }
                            }
                        }
                        is Response.Error -> {

                        }
                        else -> {

                        }
                    }
                }
        }
        getUsersChatList()
    }


    fun openChatRoom(navigation:NavController,user2Id: String) = viewModelScope.launch {

        when(val result = openChatRoomUseCase(currentUserId,user2Id)){
            is Response.Success -> {
                navigation.navigate(Screens.MessageScreen.createRoute(result.data))
            }
            is Response.Error -> {
                _error.value = result.message
            }
            else -> {

            }
        }
    }
    fun createChatRoom(user: String,navigation:NavController) = viewModelScope.launch {
        val randomUUID = java.util.UUID.randomUUID().toString()
        when (val response = createChatRoomUseCase.invoke(currentUserId,user,randomUUID)
        ) {
            is Response.Success -> {
                navigation.navigate(Screens.MessageScreen.createRoute(response.data))
            }

            is Response.Error -> {
                navigation.navigate(Screens.MessageScreen.createRoute(response.message))
            }

            else -> {

            }
        }
    }


    private fun getUsersChatList() = viewModelScope.launch{

        val listOfChats: MutableList<Chats> = mutableListOf()
        getUserChatListUseCase.invoke().collect {
            when(it){
                is Response.Success -> {
                    it.data.forEach {
                        val response = getUserUseCase.getUserData(it.users!!.filter { it != currentUserId }.first())
                        when(response){
                            is Response.Success -> {
                                 listOfChats += Chats(
                                    chatRoomId = it.chatId.toString(),
                                    name = response.data.name,
                                    surname = response.data.surname,
                                    imageUrl = response.data.profileImageUrl,
                                    isOnline = response.data.status
                                    )
                            }
                            is Response.Error -> {
                                _error.value = response.message
                            }
                            else -> {

                            }
                        }
                    }
                    _chatUserList.value = listOfChats
                }
                is Response.Error -> {
                    _error.value = it.message
                }
                else -> {

                }
            }
        }


    }
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

}