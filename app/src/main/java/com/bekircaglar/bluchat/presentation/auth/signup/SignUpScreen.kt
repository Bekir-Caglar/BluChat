package com.bekircaglar.bluchat.presentation.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.bekircaglar.bluchat.data.repository.PasswordValidatorImpl
import com.bekircaglar.bluchat.presentation.auth.component.PhoneVisualTransformation
import com.bekircaglar.bluchat.utils.passwordBorder

@Composable
fun SignUpScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var emailRegister by remember { mutableStateOf("") }
    var passwordRegister by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val passwordValidator = remember { PasswordValidatorImpl() }
    fun isPasswordValid(password: String): Boolean {
        return passwordValidator.isPasswordValid(password)
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
                            text = stringResource(R.string.title_signup),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 30.dp),
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                navigationIcon = Icons.Default.KeyboardArrowLeft,
                onNavigateIconClicked = {
                    navController.navigate(Screens.SingInScreen.route)
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(paddingValues = it)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.TopStart,
                    modifier = Modifier.weight(1f)
                ) {
                    AuthTextField(
                        hint = "Name",
                        value = name,
                        onValueChange = { name = it.replaceFirstChar { char -> char.uppercase() } },
                        leadingIcon = Icons.Default.Person,
                        keyboardType = KeyboardType.Text,
                        title = "Name"
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    contentAlignment = Alignment.TopStart,
                    modifier = Modifier.weight(1f)
                ) {
                    AuthTextField(
                        hint = "Surname",
                        value = surname,
                        onValueChange = {
                            surname = it.replaceFirstChar { char -> char.uppercase() }
                        },
                        leadingIcon = Icons.Default.Person,
                        keyboardType = KeyboardType.Text,
                        title = "Surname"
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                AuthTextField(
                    hint = stringResource(R.string.enter_your_phone_number),
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    title = stringResource(R.string.phone_number),
                    visualTransformation = PhoneVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                AuthTextField(
                    hint = stringResource(R.string.enter_your_email),
                    value = emailRegister,
                    onValueChange = { emailRegister = it },
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    title = stringResource(R.string.e_mail)
                )
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AuthTextField(
                        hint = stringResource(R.string.enter_your_password),
                        value = passwordRegister,
                        onValueChange = { passwordRegister = it },
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        title = stringResource(R.string.password),
                        supportedTextList = listOf(
                            "8 characters" to (passwordRegister.length >= 8),
                            "Minimum one number" to passwordRegister.any { it.isDigit() },
                            "Minimum one uppercase letter" to passwordRegister.any { it.isUpperCase() },
                            "Minimum one lowercase letter" to passwordRegister.any { it.isLowerCase() }
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    AuthTextField(
                        hint = "Confirm password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        title = "Confirm password",
                        modifier = Modifier.passwordBorder(passwordRegister == confirmPassword),
                    )

                    Spacer(modifier = Modifier.padding(top = 32.dp))

                    AuthButton(
                        onClick = {
                            if (!isPasswordValid(passwordRegister)) {
                                passwordError =
                                    "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number."
                            } else if (passwordRegister != confirmPassword) {
                                passwordError = "Passwords do not match."
                            } else {
                                passwordError = null
                                if (emailRegister.isEmpty() || passwordRegister.isEmpty() || name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
                                    ShowToastMessage(
                                        context = context,
                                        message = "Please fill in all fields."
                                    )
                                    return@AuthButton
                                }
                                viewModel.checkPassword(
                                    email = emailRegister,
                                    password = passwordRegister,
                                    name = name,
                                    surname = surname,
                                    phoneNumber = phoneNumber,
                                    navController = navController,
                                    context = context
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background,
                        buttonText = stringResource(R.string.title_signup),
                    )

                    passwordError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp),
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
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
    if (uiState is UiState.Error) {
        (uiState as UiState.Error).message?.let { ShowToastMessage(context, it) }
    }

}