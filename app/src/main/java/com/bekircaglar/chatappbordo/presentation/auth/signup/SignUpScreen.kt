package com.bekircaglar.chatappbordo.presentation.auth.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bekircaglar.chatappbordo.R
import com.bekircaglar.chatappbordo.navigation.Screens
import com.bekircaglar.chatappbordo.presentation.ShowToastMessage
import com.bekircaglar.chatappbordo.presentation.auth.component.AuthButton
import com.bekircaglar.chatappbordo.presentation.auth.component.AuthTextField
import com.bekircaglar.chatappbordo.presentation.component.ChatAppTopBar

@Composable
fun SignUpScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var emailRegister by remember { mutableStateOf("") }
    var passwordRegister by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                            modifier = Modifier.padding(end = 30.dp)

                        )
                    }
                },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.padding(vertical = 12.dp))

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                AuthTextField(
                    hint = { Text(text = "Name")},
                    value = name,
                    onValueChange = { name = it },
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    title = "Name"
                )

            }
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                AuthTextField(
                    hint = { Text(text = "Surname") },
                    value = surname,
                    onValueChange = { surname = it },
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    title = "Surname"
                )

            }
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                AuthTextField(
                    hint = { Text(text = stringResource(R.string.enter_your_phone_number)) },
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    title = stringResource(R.string.phone_number)
                )

            }
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                AuthTextField(
                    hint = { Text(text = stringResource(R.string.enter_your_email)) },
                    value = emailRegister,
                    onValueChange = { emailRegister = it },
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    title = stringResource(R.string.e_mail)
                )

            }
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Column {

                    AuthTextField(
                        hint = { Text(text = stringResource(R.string.enter_your_password)) },
                        value = passwordRegister,
                        onValueChange = { passwordRegister = it },
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        title = stringResource(R.string.password)
                    )

                    Spacer(modifier = Modifier.padding(top = 16.dp))

                    AuthButton(
                        onClick = {
                            viewModel.checkPassword(
                                email = emailRegister,
                                password = passwordRegister,
                                name = name,
                                surname = surname,
                                phoneNumber = phoneNumber,
                                onSuccess = {
                                    navController.navigate(Screens.HomeNav.route)
                                },
                                onError = {errorMessage ->
                                    ShowToastMessage(context = context, message = errorMessage)
                                }

                            )

                        },
                        buttonText = stringResource(R.string.title_signup),
                    )

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
                        onClick = { },
                        buttonIcon = painterResource(R.drawable.ic_google),
                        buttonText = stringResource(R.string.google_login)
                    )

                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    AuthButton(
                        onClick = { },
                        buttonIcon = painterResource(R.drawable.ic_facebook),
                        buttonText = stringResource(R.string.facebook_login),
                        containerColor = colorResource(id = R.color.facebook),
                        contentColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(modifier = Modifier.padding(vertical = 12.dp))

                }

            }
        }

    }

}
