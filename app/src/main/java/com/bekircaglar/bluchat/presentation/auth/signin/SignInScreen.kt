package com.bekircaglar.bluchat.presentation.auth.signin

import EnterPhoneNumberDialog
import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bekircaglar.bluchat.navigation.Screens
import com.bekircaglar.bluchat.presentation.ShowToastMessage
import com.bekircaglar.bluchat.presentation.auth.component.AuthButton
import com.bekircaglar.bluchat.presentation.auth.component.AuthTextField
import com.bekircaglar.bluchat.presentation.component.ChatAppTopBar
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.presentation.auth.component.LoginFacebookButton
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun SignInScreen(navController: NavController) {
    val viewModel: SignInViewModel = hiltViewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val callbackManager = remember { CallbackManager.Factory.create() }
    var phoneNumberDialogState by remember { mutableStateOf(false) }

    val googleUser = GoogleSignIn.getLastSignedInAccount(context)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.initGoogleSignInClient(context)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleGoogleSignInResult(
                task,
                onSuccess = {
                    navController.navigate(Screens.HomeNav.route) {
                        popUpTo(Screens.AuthNav.route) { inclusive = true }
                    }
                },
                onError = {
                    ShowToastMessage(context, it)
                },
                onPhoneNumberNotExist = {
                    phoneNumberDialogState = true
                },
            )
        } else {
            ShowToastMessage(context, "Google Sign-In canceled")
        }
    }

    if (phoneNumberDialogState) {
        EnterPhoneNumberDialog(
            onConfirm = { phoneNumber ->
                viewModel.saveCurrentGoogleUserToDatabase(
                    phoneNumber = phoneNumber,
                    user = googleUser,
                    onSuccess = {
                        navController.navigate(Screens.HomeNav.route) {
                            popUpTo(Screens.AuthNav.route) { inclusive = true }
                        }
                    })
            },
            onDismiss = {
                phoneNumberDialogState = false
                viewModel.signOut(context)
            }
        )
    }
    if (uiState is UiState.Error){
        (uiState as UiState.Error).message?.let { ShowToastMessage(context, it) }
    }

    Scaffold(
        topBar = {
            ChatAppTopBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.title_login),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 30.dp)
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(paddingValues = it)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                AuthTextField(
                    hint = stringResource(R.string.enter_your_email),
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    title = stringResource(R.string.e_mail)
                )
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AuthTextField(
                        hint = stringResource(R.string.enter_your_password),
                        value = password,
                        onValueChange = { password = it },
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        title = stringResource(R.string.password)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = stringResource(R.string.forgot_password),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { }
                        )
                    }
                    AuthButton(
                        onClick = {
                            viewModel.signIn(email, password,
                                onSuccess = {
                                    navController.navigate(Screens.HomeNav.route) {
                                        popUpTo(Screens.AuthNav.route) { inclusive = true }
                                    }
                                },
                                onError = {
                                    ShowToastMessage(context, it)
                                }
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background,
                        buttonText = stringResource(R.string.title_login),
                    )

                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dont_have_account),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable {
                                navController.navigate(Screens.SingUpScreen.route)
                            },
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        stringResource(R.string.policy),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.padding(vertical = 16.dp))
                    HorizontalDivider(modifier = Modifier)
                    Spacer(modifier = Modifier.padding(vertical = 16.dp))

                    AuthButton(
                        onClick = {
                            googleSignInLauncher.launch(viewModel.getGoogleSignInIntent())
                        },
                        buttonIcon = painterResource(id = R.drawable.ic_google),
                        buttonText = stringResource(R.string.google_login),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    )

                    LoginFacebookButton(
                        onAuthError = {
                            ShowToastMessage(context, it.message.toString())
                        },
                        iconRes = R.drawable.ic_facebook,
                        buttonText = stringResource(R.string.facebook_login),
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        handleFacebookSignInResult = {
                            viewModel.handleFacebookSignInResult(
                                result = it,
                                onSuccess = {
                                    navController.navigate(Screens.HomeNav.route) {
                                        popUpTo(Screens.AuthNav.route) { inclusive = true }
                                    }
                                },
                                onPhoneNumberNotExist = {
                                    phoneNumberDialogState = true
                                },
                            )
                        }
                    )
                }
            }
        }
    }
    if (uiState is UiState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center))
        }
    }
}