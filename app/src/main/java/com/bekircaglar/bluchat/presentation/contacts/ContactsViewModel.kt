package com.bekircaglar.bluchat.presentation.contacts

import android.view.View
import androidx.benchmark.perfetto.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.contact.AddContactUseCase
import com.bekircaglar.bluchat.domain.usecase.contact.GetContactsUseCase
import com.bekircaglar.bluchat.domain.usecase.message.GetMessageByIdUseCase
import com.bekircaglar.bluchat.domain.usecase.profile.GetUserUseCase
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getContactsUseCase: GetContactsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val addContactUseCase: AddContactUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _contacts = MutableStateFlow<List<Users>>(emptyList())
    val contacts: StateFlow<List<Users>> = _contacts

    private val authStateListener = FirebaseAuth.AuthStateListener {
        val user = auth.currentUser
        if (user != null) {
            getContacts(user.uid)
        } else {
            _contacts.value = emptyList()
            _uiState.value = UiState.Idle
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        auth.currentUser?.uid?.let { getContacts(it) }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun addContact(phoneNumber: String) = viewModelScope.launch {
        _uiState.value = UiState.Loading

        addContactUseCase(phoneNumber, auth.currentUser?.uid.toString()).collect {
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

                is Response.Idle -> {
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    fun getContacts(userId: String) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        getContactsUseCase(userId = userId).collect {
            when (it) {
                is Response.Success -> {
                    getUsersById(it.data)
                    _uiState.value = UiState.Success()
                }

                is Response.Error -> {
                    _uiState.value = UiState.Error(it.message)
                }

                is Response.Loading -> {
                    _uiState.value = UiState.Loading
                }

                is Response.Idle -> {
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    private fun getUsersById(userIdList: List<String?>) {
        _uiState.value = UiState.Loading
        userIdList.forEach {
            if (it != null) {
                viewModelScope.launch {
                    getUserUseCase.getUserData(it).collect {
                        when (it) {
                            is Response.Success -> {
                                if (!_contacts.value.contains(it.data)) {
                                    _contacts.value += it.data
                                }
                                _uiState.value = UiState.Success()
                            }

                            is Response.Error -> {
                                _uiState.value = UiState.Error(it.message)
                            }

                            is Response.Loading -> {
                                _uiState.value = UiState.Loading
                            }

                            is Response.Idle -> {
                                _uiState.value = UiState.Idle
                            }
                        }
                    }
                }
            }
        }
    }
}