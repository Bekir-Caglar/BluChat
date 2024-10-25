package com.bekircaglar.bluchat.presentation.contacts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bekircaglar.bluchat.presentation.bottomappbar.ChatAppBottomAppBar
import com.bekircaglar.bluchat.presentation.chat.component.ChatAppFAB
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.presentation.contacts.component.ContactItem
import com.bekircaglar.bluchat.ui.theme.ChatAppBordoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(navController: NavController) {

    val viewModel : ContactsViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val contacts by viewModel.contacts.collectAsStateWithLifecycle()

    var addContactSheetState by remember { mutableStateOf(false) }

    if (addContactSheetState) {
        AddContactSheet(onSave = { phoneNumber ->
            viewModel.addContact(phoneNumber)
            addContactSheetState = false
        }, onDismissRequest = {
            addContactSheetState = false
        })
    }


    Scaffold(
        topBar = {
            ChatAppTopBar(
                title = {
                    Text(text = "Contacts")
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                actionIcon = Icons.Default.Search,
            )
        },
        bottomBar = {
            ChatAppBottomAppBar(navController = navController)
        },
        floatingActionButton = {
            ChatAppFAB(
                onClick = {
                    addContactSheetState = true
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            val groupedContacts = contacts.sortedBy { it.name }.groupBy { contact ->
                contact.name.first().uppercaseChar()
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                groupedContacts.forEach { (initial, contactList) ->
                    stickyHeader {
                        Text(
                            text = initial.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.background.copy(0.8f))
                                .padding(4.dp)
                                .padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Start
                        )
                    }

                    items(contactList) { contact ->
                        ContactItem(
                            name = contact.name + " " + contact.surname,
                            phoneNumber = contact.phoneNumber,
                            profileImageUrl = contact.profileImageUrl
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactSheet(onSave: (String) -> Unit,onDismissRequest: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
            }
            onDismissRequest()

        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Contact",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(text = "Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSave(phoneNumber)
                    scope.launch {
                        sheetState.hide()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save Contact")
            }
        }
    }
}