package com.bekircaglar.chatappbordo.presentation.profile

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.usecase.CheckPhoneNumberUseCase
import com.bekircaglar.chatappbordo.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.chatappbordo.domain.usecase.auth.SignOutUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.UpdateUserUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.handleCoroutineException
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

    init {
        getUserProfile()
    }


    fun checkPhoneNumber(surname:String,name: String,phoneNumber: String? = "",profileImage:String, onSuccess: () -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        when(val result = checkPhoneNumberUseCase.checkPhoneNumber(phoneNumber?:"")){
            is Response.Success -> {
                onSuccess()
                updateUserData(
                    surname = surname,
                    name = name,
                    phoneNumber = phoneNumber,
                    profileImage = profileImage
                )

            }
            is Response.Error -> {
                onError(exceptionHandlerUseCase.invoke(Exception(result.message)))
            }
            else -> {
                onError("Unknown Error")
            }
        }
    }
    private fun updateUserData(surname:String,name: String,phoneNumber: String? = "",profileImage:String) = viewModelScope.launch {
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
        getUserUseCase.invoke().collect {
            _users.value = it

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




    fun signOut(onSuccess: (String) -> Unit, onError:(String) -> Unit) = viewModelScope.launch {
        val result = signOutUseCase.invoke()
        when (result) {
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