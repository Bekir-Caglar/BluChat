package com.bekircaglar.bluchat.presentation.chatinfo

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.auth.AuthUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.AddParticipantUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.DeleteGroupUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.KickUserUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.LeaveChatUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.UpdateChatInfoUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.SearchPhoneNumberUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UploadImageUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    private val getChatRoomUseCase: GetChatRoomUseCase,
    private val getUserFromChatIdUseCase: GetUserFromChatIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val authUseCase: FirebaseAuth,
    private val leaveChatUseCase: LeaveChatUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val kickUserUseCase: KickUserUseCase,
    private val addParticipantUseCase: AddParticipantUseCase,
    private val searchPhoneNumberUseCase: SearchPhoneNumberUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val updateChatInfoUseCase: UpdateChatInfoUseCase

    ) : ViewModel() {

    var currentUser = authUseCase.currentUser!!

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Users>>(emptyList())
    val searchResults: StateFlow<List<Users>> = _searchResults.asStateFlow()

    private val _chatUserList = MutableStateFlow<List<Users>>(emptyList())
    val chatUserList = _chatUserList.asStateFlow()

    private val _chatUserIdList = MutableStateFlow<List<String?>>(emptyList())
    val chatUserIdList = _chatUserIdList.asStateFlow()

    private val _chatRoom = MutableStateFlow<ChatRoom>(ChatRoom())
    val chatRoom = _chatRoom.asStateFlow()


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
                                it.filter { user -> user.uid != authUseCase.currentUser?.uid }
                            }
                        }

                        is Response.Error -> {

                        }

                        else -> {

                        }
                    }
                }
        }
    }
    fun updateChatInfo(chatId: String, chatName: String,chatImageUrl:String) = viewModelScope.launch {

        updateChatInfoUseCase(chatId, chatName, chatImageUrl)
    }



    fun getChatRoom(chatId: String) = viewModelScope.launch {
        getChatRoomUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    _chatRoom.value = response.data
                    getUsersFromChatId(chatId)
                }

                is Response.Error -> {
                }
            }
        }
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
                    }
                    else -> {
                    }
                }
            }
        }

    }

    private fun getUsersFromChatId(chatId: String) = viewModelScope.launch {

        getUserFromChatIdUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    val userIdList = response.data
                    _chatUserIdList.value = userIdList
                    userIdList + currentUser.uid
                    getUsersFromUserId(userIdList)

                }

                is Response.Error -> {
                }
            }
        }

    }

    private fun getUsersFromUserId(userIdList: List<String?>) {
        viewModelScope.launch {
            val groupMembers = mutableListOf<Users>()

            for (userId in userIdList) {
                when (val response = getUserUseCase.getUserData(userId!!)) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {
                        val user = response.data
                        groupMembers.add(user)
                    }

                    is Response.Error -> {
                    }
                }
            }
            _chatUserList.value = groupMembers
        }

    }

    fun leaveChat(chatId: String) = viewModelScope.launch {
        leaveChatUseCase(userId = currentUser.uid, chatId = chatId)

    }
    fun deleteGroup(chatId: String) = viewModelScope.launch {
        deleteGroupUseCase(chatId)

    }
    fun kickUser(chatId: String, userId: String) = viewModelScope.launch {
        kickUserUseCase(userId, chatId)

    }
    fun addParticipant(chatId: String, userIdList: List<String?>) = viewModelScope.launch {
        addParticipantUseCase(chatId, userIdList)
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

}