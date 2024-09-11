package com.bekircaglar.bluchat.presentation.chat

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.chats.CreateChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.CreateGroupChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.GetUserChatListUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.OpenChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.SearchPhoneNumberUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UploadImageUseCase
import com.bekircaglar.bluchat.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val searchPhoneNumberUseCase: SearchPhoneNumberUseCase,
    private val getUserChatListUseCase: GetUserChatListUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val openChatRoomUseCase: OpenChatRoomUseCase,
    private val createGroupChatRoomUseCase: CreateGroupChatRoomUseCase,
    private val auth: FirebaseAuth,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {


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

    private val _selectedImageUri = MutableStateFlow<android.net.Uri?>(null)
    val selectedImageUri: StateFlow<android.net.Uri?> = _selectedImageUri

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri


    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
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

    fun onImageSelected(uri: android.net.Uri) {
        _selectedImageUri.value = uri
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri){
        viewModelScope.launch {
            _isLoading.value = true
            uploadImageUseCase.invoke(uri).collect{
                when(it){
                    is Response.Success -> {
                        _uploadedImageUri.value = it.data.toUri()
                        _isLoading.value = false

                    }
                    is Response.Error -> {
                        _error.value = it.message
                    }
                    else -> {
                        _error.value = "Unknown Error"
                    }
                }
            }
        }

    }


    fun createGroupChatRoom(groupMembers:List<String>,groupName: String, firebaseImageUrl: String) = viewModelScope.launch {

        val randomUUID = java.util.UUID.randomUUID().toString()

        when (val response =
            createGroupChatRoomUseCase(currentUserId,groupMembers, randomUUID, groupName, firebaseImageUrl)
        ) {
            is Response.Success -> {
                _succes.value = response.data
            }

            is Response.Error -> {
                _error.value = response.message
            }

            else -> {

            }
        }

    }

    fun createChatRoom(user: String, navigation: NavController) = viewModelScope.launch {
        val randomUUID = java.util.UUID.randomUUID().toString()
        when (val response = createChatRoomUseCase(currentUserId, user, randomUUID)
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

    private fun getUsersChatList() = viewModelScope.launch {
        getUserChatListUseCase.invoke().collect { response ->
            _chatUserList.value = emptyList()
            when (response) {
                is Response.Success -> {

                    response.data.forEach { chat ->
                        if (chat.chatType == "private") {
                            getUserFromChat(chat)
                        } else if (chat.chatType == "group") {
                             _chatUserList.value += Chats(
                                chatRoomId = chat.chatId.toString(),
                                name = chat.chatName!!,
                                imageUrl = chat.chatImage!!,
                                isOnline = false
                            )
                        }
                    }
                }

                is Response.Error -> {
                    _error.value = response.message
                }

                else -> {
                    // Handle other cases if necessary
                }
            }
        }
    }

    private fun getUserFromChat(chat: ChatRoom) = viewModelScope.launch{
        val listOfChats: MutableList<Chats> = mutableListOf()
        val userId = chat.users!!.firstOrNull { it != currentUserId }
        if (userId != null) {
            when (val userResponse = getUserUseCase.getUserData(userId)) {
                is Response.Success -> {
                    _chatUserList.value += Chats(
                        chatRoomId = chat.chatId.toString(),
                        name = userResponse.data.name,
                        surname = userResponse.data.surname,
                        imageUrl = userResponse.data.profileImageUrl,
                        isOnline = userResponse.data.status
                    )
                }

                is Response.Error -> {
                    _error.value = userResponse.message
                }

                else -> {
                    // Handle other cases if necessary
                }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

}