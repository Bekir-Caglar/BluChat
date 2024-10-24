package com.bekircaglar.bluchat.presentation.message

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.utils.GROUP
import com.bekircaglar.bluchat.utils.PRIVATE
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.model.Messages
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.message.CreateMessageRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.DeleteMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.EditMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetChatRoomUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetMessageByIdUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetPinnedMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetStarredMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetUserFromChatIdUseCase
import com.bekircaglar.bluchat.domain.usecase.message.LoadInitialMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.LoadMoreMessagesUseCase
import com.bekircaglar.bluchat.domain.usecase.message.MarkMessageAsReadUseCase
import com.bekircaglar.bluchat.domain.usecase.message.ObserveGroupStatusUseCase
import com.bekircaglar.bluchat.domain.usecase.message.ObserveUserStatusInGroupUseCase
import com.bekircaglar.bluchat.domain.usecase.message.PinMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.SetLastMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.StarMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.UnPinMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.UnStarMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.message.UploadVideoUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UploadImageUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getUserFromChatIdUseCase: GetUserFromChatIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createMessageRoomUseCase: CreateMessageRoomUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val loadInitialMessagesUseCase: LoadInitialMessagesUseCase,
    private val loadMoreMessagesUseCase: LoadMoreMessagesUseCase,
    private val getChatRoomUseCase: GetChatRoomUseCase,
    private val observeGroupStatusUseCase: ObserveGroupStatusUseCase,
    private val observeUserStatusInGroupUseCase: ObserveUserStatusInGroupUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val pinMessageUseCase: PinMessageUseCase,
    private val unPinMessageUseCase: UnPinMessageUseCase,
    private val getPinnedMessagesUseCase: GetPinnedMessagesUseCase,
    private val getStarredMessagesUseCase: GetStarredMessagesUseCase,
    private val starMessageUseCase: StarMessageUseCase,
    private val unStarMessageUseCase: UnStarMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val uploadVideoUseCase: UploadVideoUseCase,
    private val setLastMessageUseCase: SetLastMessageUseCase,
    private val getMessageByIdUseCase: GetMessageByIdUseCase
) : ViewModel() {

    val currentUser = auth.currentUser!!


    private val _userData = MutableStateFlow<Users?>(null)
    var userData = _userData.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var lastKey: String? = null

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri

    private val _uploadedVideoUri = MutableStateFlow<Uri?>(null)
    val uploadedVideoUri: StateFlow<Uri?> = _uploadedVideoUri

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _moreMessageState = MutableStateFlow<UiState>(UiState.Idle)
    val moreMessageState = _moreMessageState.asStateFlow()

    private val _pinnedMessages = MutableStateFlow<List<Message>>(emptyList())
    val pinnedMessages: StateFlow<List<Message>> = _pinnedMessages

    private val _starredMessages = MutableStateFlow<List<Message>>(emptyList())
    val starredMessages: StateFlow<List<Message>> = _starredMessages


    private val _isKickedOrGroupDeleted = MutableStateFlow(false)
    val isKickedOrGroupDeleted: StateFlow<Boolean> = _isKickedOrGroupDeleted


    init {
        _uiState.value = UiState.Loading
    }

    fun markMessageAsRead(messageId: String, chatId: String) {
        viewModelScope.launch {
            markMessageAsReadUseCase(messageId, chatId).collect {
                when (it) {
                    is Response.Success -> {
                    }

                    is Response.Error -> {
                    }

                    is Response.Loading -> {
                    }

                    else -> {
                    }

                }
            }

        }
    }

    fun pinMessage(message: Message, chatId: String) = viewModelScope.launch {
        pinMessageUseCase(message.messageId!!, chatId).collect {
            when (it) {
                is Response.Success -> {
                }

                is Response.Error -> {
                }

                is Response.Loading -> {}
                else -> {

                }
            }
        }
    }

    fun unPinMessage(message: Message, chatId: String) = viewModelScope.launch {
        unPinMessageUseCase(message.messageId!!, chatId).collect {
            when (it) {
                is Response.Success -> {
                }

                is Response.Error -> {
                }

                is Response.Loading -> {
                }

                else -> {

                }
            }
        }
    }

    fun starMessage(message: Message, chatId: String) = viewModelScope.launch {
        starMessageUseCase(message.messageId!!, chatId).collect {
            when (it) {
                is Response.Success -> {

                }

                is Response.Error -> {
                }

                is Response.Loading -> {
                }

                else -> {

                }
            }
        }
    }

    fun unStarMessage(message: Message, chatId: String) = viewModelScope.launch {
        unStarMessageUseCase(message.messageId!!, chatId).collect {
            when (it) {
                is Response.Success -> {

                }

                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                else -> {

                }
            }
        }
    }

    fun observeGroupAndUserStatus(groupId: String, userId: String) {
        viewModelScope.launch {
            observeGroupStatusUseCase(groupId).collect { isGroupDeleted ->
                if (isGroupDeleted) {
                    _isKickedOrGroupDeleted.value = true
                }
            }
        }
        viewModelScope.launch {
            observeUserStatusInGroupUseCase(groupId, userId).collect { isUserKicked ->
                if (isUserKicked) {
                    _isKickedOrGroupDeleted.value = true
                }
            }
        }
    }


    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        uploadImage(uri)
    }

    fun onVideoSelected(uri: Uri) {
        _selectedImageUri.value = uri
        uploadVideo(uri)
    }

    private fun uploadVideo(uri: Uri) {
        viewModelScope.launch {
            uploadVideoUseCase(uri).collect {
                when (it) {
                    is Response.Success -> {
                        _uploadedVideoUri.value = it.data.toUri()
                    }

                    is Response.Error -> {

                    }

                    is Response.Loading -> {

                    }

                    else -> {

                    }
                }
            }

        }

    }

    private fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            uploadImageUseCase.invoke(uri).collect {
                when (it) {
                    is Response.Success -> {
                        _uploadedImageUri.value = it.data.toUri()
                    }

                    is Response.Error -> {
                    }

                    else -> {
                    }
                }
            }
        }

    }

    fun getPinnedMessages(chatId: String) = viewModelScope.launch {
        getPinnedMessagesUseCase(chatId).collect { pinnedMessages ->
            when (pinnedMessages) {
                is Response.Success -> {
                    _pinnedMessages.value = pinnedMessages.data
                }

                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                else -> {

                }
            }
        }
    }

    fun getStarredMessages(chatId: String) = viewModelScope.launch {
        getStarredMessagesUseCase(chatId).collect { starredMessages ->

            when (starredMessages) {
                is Response.Success -> {
                    _starredMessages.value = starredMessages.data
                }

                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                else -> {

                }

            }

        }

    }

    fun loadInitialMessages(chatId: String) {
        viewModelScope.launch {
            loadInitialMessagesUseCase(chatId).collect { messages ->
                when (messages) {
                    is Response.Success -> {
                        _messages.value = messages.data.reversed()
                        lastKey = messages.data.lastOrNull()?.messageId
                        _uiState.value = UiState.Success()
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

    fun loadMoreMessages(moreLastKey: String?, chatId: String) {
        _moreMessageState.value = UiState.Loading
        moreLastKey?.let { moreLastKey ->
            viewModelScope.launch {
                loadMoreMessagesUseCase(chatId, moreLastKey).collect { moreMessages ->
                    when (moreMessages) {
                        is Response.Success -> {
                            _messages.value += moreMessages.data.reversed()
                                .distinctBy { it.messageId }
                            _moreMessageState.value = UiState.Success()

                        }

                        is Response.Error -> {
                            _moreMessageState.value = UiState.Error(moreMessages.message)
                        }

                        is Response.Loading -> {
                            _moreMessageState.value = UiState.Loading

                        }

                        else -> {
                            _moreMessageState.value = UiState.Idle
                        }
                    }
                }
            }
        }
    }

     fun getMessageById(messageId: String, chatId: String,onResult: (Message) -> Unit) = viewModelScope.launch {

        getMessageByIdUseCase(messageId, chatId).collect {
            when (it) {
                is Response.Success -> {
                    onResult(it.data)
                }

                is Response.Error -> {

                }

                is Response.Loading -> {

                }

                else -> {
                }

            }
        }
     }


    fun sendMessage(
        imageUrl: String? = "",
        message: String,
        chatId: String,
        messageType: String,
        replyTo: String? = ""
    ) = viewModelScope.launch {

        val timestamp = System.currentTimeMillis()
        val randomId = "$timestamp-${UUID.randomUUID()}"


        val myMessage = Message(
            messageId = randomId,
            senderId = currentUser.uid,
            message = message,
            timestamp = timestamp,
            read = false,
            messageType = messageType,
            imageUrl = imageUrl,
            replyTo = replyTo
        )

        sendMessageUseCase(myMessage, chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    setLastMessage(myMessage, chatId)
                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }
    }

    private fun setLastMessage(message: Message, chatId: String) = viewModelScope.launch {
        setLastMessageUseCase(chatId, message).collect {
            when (it) {
                is Response.Success -> {
                }

                is Response.Error -> {
                }

                is Response.Loading -> {
                }

                else -> {
                }
            }
        }
    }


    fun createMessageRoom(chatId: String) = viewModelScope.launch {
        createMessageRoomUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }
    }

    fun getChatRoom(chatId: String) = viewModelScope.launch {
        getChatRoomUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    if (response.data.chatType == GROUP) {
                        val myGroupAsUser = Users(
                            name = response.data.chatName!!,
                            profileImageUrl = response.data.chatImage!!,
                        )
                        _userData.value = myGroupAsUser
                    } else if (response.data.chatType == PRIVATE) {
                        getUserFromChatId(chatId)
                    }
                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }
    }

    private fun getUserFromChatId(chatId: String) = viewModelScope.launch {

        getUserFromChatIdUseCase(chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    val userId = response.data[0]

                    println("userId: $userId")

                    getUserFromUserId(userId)

                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }

    }

    fun deleteMessage(messageId: String, chatId: String) = viewModelScope.launch {
        deleteMessageUseCase(messageId, chatId).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }
    }

    fun editMessage(messageId: String, chatId: String, message: String) = viewModelScope.launch {
        editMessageUseCase(messageId, chatId, message).collect { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                }

                is Response.Error -> {
                }

                else -> {

                }
            }
        }
    }


    fun getUserFromUserId(userId: String?) {
        viewModelScope.launch {
            getUserUseCase.getUserData(userId!!).collect { response ->
                when (response) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {
                        _userData.value = response.data
                    }

                    is Response.Error -> {
                    }

                    else -> {

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

    private val userColorCache = mutableMapOf<String, Color>()

    fun getUserColor(userId: String): Color {
        return userColorCache.getOrPut(userId) {
            Color(
                red = (0..255).random() / 255f,
                green = (0..255).random() / 255f,
                blue = (0..255).random() / 255f
            )
        }
    }

}