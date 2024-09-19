package com.bekircaglar.bluchat.presentation.message.camera

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.usecase.message.SendMessageUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.UploadImageUseCase
import com.bekircaglar.bluchat.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val uploadImageUseCase: UploadImageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    auth: FirebaseAuth
):ViewModel() {

    private val _currentUser = auth.currentUser!!


    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri


    fun onImageSelected(uri: Uri,navController: NavController,chatId: String) {
        uploadImage(uri,navController,chatId)
    }

    private fun uploadImage(uri: Uri,navController: NavController,chatId: String) {
        viewModelScope.launch {

            uploadImageUseCase.invoke(uri).collect {
                when (it) {
                    is Response.Success -> {
                        _uploadedImageUri.value = it.data.toUri()
                        val encodedUrl = URLEncoder.encode(it.data, StandardCharsets.UTF_8.toString())
                        navController.navigate(Screens.SendTakenPhotoScreen.createRoute(encodedUrl,chatId))
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