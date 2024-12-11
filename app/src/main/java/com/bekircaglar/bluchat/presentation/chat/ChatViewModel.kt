package com.bekircaglar.bluchat.presentation.chat

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bekircaglar.bluchat.utils.GROUP
import com.bekircaglar.bluchat.utils.PRIVATE
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.data.repository.ChatRepositoryImp
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Chats
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.chats.CreateChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.CreateGroupChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.chats.GetUserChatListUseCase
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val searchPhoneNumberUseCase: SearchPhoneNumberUseCase,
    private val getUserChatListUseCase: GetUserChatListUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val createGroupChatRoomUseCase: CreateGroupChatRoomUseCase,
    private val auth: FirebaseAuth,
    private val uploadImageUseCase: UploadImageUseCase,

    ) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Users>>(emptyList())
    val searchResults: StateFlow<List<Users>> = _searchResults.asStateFlow()

    private val _chatUserList = MutableStateFlow<List<Chats>>(emptyList())
    val chatUserList = _chatUserList.asStateFlow()

    private val currentUserId = auth.currentUser?.uid.toString()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private var stateOfGroup = MutableStateFlow<UiState>(UiState.Loading)
    private val stateOfPrivate = MutableStateFlow<UiState>(UiState.Loading)

    private val _UploadImageState = MutableStateFlow<UiState>(UiState.Idle)
    val UploadImageState: StateFlow<UiState> = _UploadImageState

    private val _chatPPImageState = MutableStateFlow<UiState>(UiState.Idle)
    val chatPPImageState: StateFlow<UiState> = _chatPPImageState

    private val _selectedUser = MutableStateFlow<Users?>(null)
    val selectedUser: StateFlow<Users?> = _selectedUser

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser


    init {
        _currentUser.value = auth.currentUser?.uid
        observeSearchQuery()
        if (stateOfPrivate.value == UiState.Success() && stateOfGroup.value == UiState.Success()) {
            _uiState.value = UiState.Success()
        }

        getUsersChatList()
    }
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.debounce(300)
                .collect { query ->
                    searchPhoneNumberUseCase(query).collect {
                        when (it) {
                            is Response.Success -> { _searchResults.value = it.data.let {
                                    it.filter { user -> user.uid != auth.currentUser?.uid }
                                }
                                _uiState.value = UiState.Success()
                            }

                            is Response.Error -> {
                                _uiState.value = UiState.Error(it.message)
                            }

                            is Response.Loading -> {
                                _uiState.value = UiState.Loading
                            }

                            else -> {
                                _uiState.value = UiState.Idle
                            }
                        }
                    }
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) = viewModelScope.launch {
        _searchQuery.value = newQuery
        observeSearchQuery()
    }

    fun changeImageState() {
        _chatPPImageState.value = UiState.Success()

    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _UploadImageState.value = UiState.Loading
            uploadImageUseCase.invoke(uri).collect {
                when (it) {
                    is Response.Success -> {
                        _uploadedImageUri.value = it.data.toUri()
                        _UploadImageState.value = UiState.Success()
                    }

                    is Response.Error -> {
                        _UploadImageState.value = UiState.Error(it.message)
                    }

                    is Response.Loading -> {
                        _UploadImageState.value = UiState.Loading
                    }

                    else -> {
                        _UploadImageState.value = UiState.Idle
                    }
                }
            }
        }
    }

    fun createGroupChatRoom(
        groupMembers: List<String>,
        groupName: String,
        firebaseImageUrl: Uri?
    ) = viewModelScope.launch {
        val randomUUID = java.util.UUID.randomUUID().toString()
        val imageUrl = firebaseImageUrl
            ?: "https://firebasestorage.googleapis.com/v0/b/chatappbordo.appspot.com/o/def_user.png?alt=media&token=54d55dc5-4fad-415a-8b6f-d0f3b0619f31".toUri()

        createGroupChatRoomUseCase.invoke(
            currentUserId,
            groupMembers,
            randomUUID,
            groupName,
            imageUrl.toString()
        ).collect {
            when (it) {
                is Response.Success -> {
                    _uiState.value = UiState.Success()
                }

                is Response.Error -> {
                    _uiState.value = UiState.Error(it.message)
                }

                is Response.Loading -> {
                    _uiState.value = UiState.Loading
                }

                else -> {
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    fun createChatRoom(user: String, navigation: NavController) = viewModelScope.launch {
        val randomUUID = java.util.UUID.randomUUID().toString()
        createChatRoomUseCase.invoke(currentUserId, user, randomUUID).collect {
            when (it) {
                is Response.Success -> {
                    navigation.navigate(Screens.MessageScreen.createRoute(it.data))
                    _uiState.value = UiState.Success()
                }

                is Response.Error -> {
                    navigation.navigate(Screens.MessageScreen.createRoute(it.message))
                    _uiState.value = UiState.Error(it.message)
                }

                is Response.Loading -> {
                    _uiState.value = UiState.Loading
                }

                else -> {
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    private fun getUsersChatList() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        getUserChatListUseCase.invoke().collect { response ->
            when (response) {
                is Response.Success -> {

                    response.data.forEach { chat ->
                        if (chat.chatType == PRIVATE) {
                            getUserFromChat(chat)
                        } else if (chat.chatType == GROUP) {
                            if (!_chatUserList.value.any { it.chatRoomId == chat.chatId.toString() }) {
                                _chatUserList.value += Chats(
                                    chatRoomId = chat.chatId.toString(),
                                    name = chat.chatName!!,
                                    imageUrl = chat.chatImage!!,
                                    lastMessageSenderId = chat.chatLastMessageSenderId!!,
                                    lastMessage = chat.chatLastMessage,
                                    messageTime = chat.chatLastMessageTime,
                                    isOnline = false
                                )
                            }
                            stateOfGroup.value = UiState.Success()
                        }
                    }
                }

                is Response.Error -> {
                    _uiState.value = UiState.Error(response.message)
                }

                is Response.Loading -> {
                    _uiState.value = UiState.Loading
                }

                else -> {
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    private fun getUserFromChat(chat: ChatRoom) = viewModelScope.launch {
        val userId = chat.users!!.firstOrNull { it != currentUserId }
        if (userId != null) {
            getUserUseCase.getUserData(userId).collect {
                when (it) {
                    is Response.Success -> {
                        val user = it.data
                        val chatItem = Chats(
                            chatRoomId = chat.chatId.toString(),
                            name = user.name,
                            surname = user.surname,
                            imageUrl = user.profileImageUrl,
                            lastMessage = chat.chatLastMessage,
                            lastMessageSenderId = chat.chatLastMessageSenderId,
                            messageTime = chat.chatLastMessageTime,
                            isOnline = user.status
                        )
                        if (!_chatUserList.value.any { it.chatRoomId == chatItem.chatRoomId }) {
                            _chatUserList.value += chatItem
                        } else {
                            _chatUserList.value = _chatUserList.value.map {
                                if (it.chatRoomId == chatItem.chatRoomId) chatItem else it
                            }
                        }
                        stateOfPrivate.value = UiState.Success()
                    }

                    is Response.Error -> {
                        _uiState.value = UiState.Error()
                    }

                    is Response.Loading -> {
                        _uiState.value = UiState.Loading
                    }

                    else -> {
                        _uiState.value = UiState.Idle
                    }
                }
            }
        }
    }
    private val userNameCache = mutableMapOf<String, String>()

    fun getUserNameFromUserId(userId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (userNameCache.containsKey(userId)) {
                onResult(userNameCache[userId]!!)
            } else {
                getUserUseCase.getUserData(userId).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            userNameCache[userId] = response.data.name
                            onResult(response.data.name)
                        }

                        is Response.Error -> {
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }
}