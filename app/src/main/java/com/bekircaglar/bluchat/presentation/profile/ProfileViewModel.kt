package com.bekircaglar.bluchat.presentation.profile

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.UiState
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.CheckPhoneNumberUseCase
import com.bekircaglar.bluchat.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.SignOutUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UpdateUserUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val checkPhoneNumberUseCase: CheckPhoneNumberUseCase
) : ViewModel() {

    private val _users = MutableStateFlow<Users?>(null)
    val users: StateFlow<Users?> = _users

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var _state = MutableStateFlow(UiState.Loading)
    val state: StateFlow<UiState> = _state

    init {
        getUserProfile()
    }


    fun checkPhoneNumber(name:String,surname: String,phoneNumber: String? = "",profileImage:String, onSuccess: () -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
       checkPhoneNumberUseCase(phoneNumber?:"").collect{
              when(it){
                is Response.Success -> {
                     updateUserData(name,surname,phoneNumber,profileImage)
                     onSuccess()
                }
                is Response.Error -> {
                     onError(it.message)
                }
                else -> {
                     onError("Unknown Error")
                }
              }
       }
    }
    private fun updateUserData(name:String,surname: String,phoneNumber: String? = "",profileImage:String) = viewModelScope.launch {
        val user = _users.value
        user?.let {
            user.name = if (name.equals("")) user.name else name
            user.surname = if (surname.equals("")) user.surname else surname
            user.phoneNumber = if (phoneNumber.isNullOrEmpty()) user.phoneNumber else phoneNumber
            user.profileImageUrl = if (profileImage.equals("null")) user.profileImageUrl else profileImage
            _users.value = user
        }
        when(val result = updateUserUseCase.invoke(user!!)){
            is Response.Success -> {

            }
            is Response.Error -> {
                _error.value = result.message
            }
            else -> {
                _error.value = "Unknown Error"
            }
        }


    }
    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        uploadImage(uri)
    }

    fun getUserProfile() = viewModelScope.launch {

        getUserUseCase.invoke().collect{
            when(it) {
                is Response.Success -> {
                    _users.value = it.data
                    _state.value = UiState.Success
                }

                is Response.Error -> {
                    _error.value = it.message
                    _state.value = UiState.Error

                }
                is Response.Loading -> {
                    _state.value = UiState.Loading
                }
            }
        }
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




    fun signOut(context: Context,onSuccess: (String) -> Unit, onError:(String) -> Unit) = viewModelScope.launch {

        when (val result = signOutUseCase(context)) {
            is Response.Success -> {
                onSuccess("Successfully SignOut")
            }
            is Response.Error -> {
                onError(
                    exceptionHandlerUseCase.invoke(Exception(result.message))
                )
            }
            else -> {
                onError("Unknown Error")
            }
        }
    }


}