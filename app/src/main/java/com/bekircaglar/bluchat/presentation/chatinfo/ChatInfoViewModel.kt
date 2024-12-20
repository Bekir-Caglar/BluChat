package com.bekircaglar.bluchat.presentation.chatinfo

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.chatinfo.AddParticipantUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.DeleteGroupUseCase
import com.bekircaglar.bluchat.domain.usecase.chatinfo.GetChatImagesUseCase
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
    private val updateChatInfoUseCase: UpdateChatInfoUseCase,
    private val getChatImagesUseCase: GetChatImagesUseCase

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

    private val _otherUser = MutableStateFlow<Users>(Users())
    val otherUser = _otherUser.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri

    private val _ChatImages = MutableStateFlow<List<String>>(emptyList())
    val ChatImages: StateFlow<List<String>> = _ChatImages

    private val _stateOfUserListState = MutableStateFlow<UiState>(UiState.Idle)
    val stateOfUserListState = _stateOfUserListState.asStateFlow()

    private val _chatImagesState = MutableStateFlow<UiState>(UiState.Idle)
    val chatImagesState = _chatImagesState.asStateFlow()

    private val _uploadImageState = MutableStateFlow<UiState>(UiState.Idle)
    val uploadImageState = _uploadImageState.asStateFlow()


    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .collect { query ->
                    searchPhoneNumberUseCase(query).collect {
                        when (it) {
                            is Response.Success -> {
                                _searchResults.value = it.data.let {
                                    it.filter { user -> user.uid != authUseCase.currentUser?.uid }
                                }
                                _stateOfUserListState.value = UiState.Success()
                            }

                            is Response.Error -> {
                                _stateOfUserListState.value = UiState.Error(it.message)
                            }

                            is Response.Loading -> {
                                _stateOfUserListState.value = UiState.Loading
                            }

                            else -> {
                                _stateOfUserListState.value = UiState.Idle
                            }
                        }

                    }

                }
        }

    }

    fun setChatImagesState(uiState: UiState) {
        _chatImagesState.value = uiState
    }

    fun getChatImages(chatId: String) = viewModelScope.launch {
        getChatImagesUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                    _chatImagesState.value = UiState.Loading
                }

                is Response.Success -> {
                    val imageUrlList = response.data
                    _ChatImages.value = imageUrlList
                }

                is Response.Error -> {
                    _chatImagesState.value = UiState.Error(response.message)
                }

                else -> {
                    _chatImagesState.value = UiState.Idle
                }
            }
        }
    }

    fun updateChatInfo(chatId: String, chatName: String, chatImageUrl: String) =
        viewModelScope.launch {
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
                    _stateOfUserListState.value = UiState.Error(response.message)
                }

                else -> {

                }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uploadImageState.value = UiState.Loading
            uploadImageUseCase.invoke(uri).collect {
                when (it) {
                    is Response.Success -> {
                        _uploadedImageUri.value = it.data.toUri()
                        _uploadImageState.value = UiState.Success()
                    }

                    is Response.Error -> {
                        _stateOfUserListState.value = UiState.Error(it.message)

                    }

                    is Response.Loading -> {
                        _uploadImageState.value = UiState.Loading
                    }

                    else -> {
                        _stateOfUserListState.value = UiState.Idle
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
                    _chatUserIdList.value = userIdList + currentUser.uid
                    getUsersFromUserId(userIdList)

                }

                is Response.Error -> {
                    _stateOfUserListState.value = UiState.Error(response.message)
                }

                else -> {
                    _stateOfUserListState.value = UiState.Idle
                }
            }
        }
    }

    private fun getUsersFromUserId(userIdList: List<String?>) = viewModelScope.launch {
        val userList = mutableListOf<Users>()
        _stateOfUserListState.value = UiState.Loading
        userIdList.forEach { userId ->
            launch {
                getUserUseCase.getUserData(userId!!).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            _stateOfUserListState.value = UiState.Loading
                        }

                        is Response.Success -> {
                            userList.add(response.data)
                            _chatUserList.value = userList
                            _otherUser.value = userList.first { it.uid != currentUser.uid }

                        }

                        is Response.Error -> {
                            _stateOfUserListState.value = UiState.Error(response.message)
                        }

                        else -> {
                            _stateOfUserListState.value = UiState.Idle
                        }
                    }
                }
            }
        }
        _stateOfUserListState.value = UiState.Success()
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