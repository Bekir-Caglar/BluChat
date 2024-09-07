package com.bekircaglar.bluchat.domain.usecase.auth

import javax.inject.Inject

data class AuthUseCase @Inject constructor(
    val isUserAuthenticatedInFirebase: IsUserAuthenticatedInFirebase,
    val signInUseCase: SignInUseCase,
    val signUpUseCase: SignUpUseCase,
)