package com.bekircaglar.bluchat.presentation.contacts

import android.view.View
import androidx.benchmark.perfetto.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.usecase.contact.AddContactUseCase
import com.bekircaglar.bluchat.domain.usecase.contact.GetAppUserContactsUseCase
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
    private val getAppUserContactsUseCase: GetAppUserContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _contacts = MutableStateFlow<List<Users>>(emptyList())
    val contacts: StateFlow<List<Users>> = _contacts

    private val addedUserIds = mutableSetOf<String>()

    fun getAppUserContacts(contactsList: List<Users>) = viewModelScope.launch {
        getAppUserContactsUseCase(contactsList, auth.currentUser?.uid.toString()).collect {
            when (it) {
                is Response.Success -> {
                    _contacts.value = it.data
                    _uiState.value = UiState.Success()
                    addAllContacts()
                }
                is Response.Error -> {
                    _uiState.value = UiState.Error(it.message)
                }
                is Response.Loading -> {
                    _uiState.value = UiState.Loading
                }
                is Response.Idle -> {
                    // Handle idle state if necessary
                }
            }
        }
    }

    fun addContact(phoneNumber: String) = viewModelScope.launch {
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

    private fun addAllContacts() = viewModelScope.launch {
        _contacts.value.forEach { user ->
            if (!addedUserIds.contains(user.uid)) {
                addContact(user.phoneNumber)
                addedUserIds.add(user.uid)
            }
        }
    }
}