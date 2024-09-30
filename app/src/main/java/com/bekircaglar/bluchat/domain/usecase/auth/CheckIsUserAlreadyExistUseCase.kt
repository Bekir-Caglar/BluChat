package com.bekircaglar.bluchat.domain.usecase.auth

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckIsUserAlreadyExistUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String) : Flow<Response<Boolean>> = authRepository.checkIsUserAlreadyExist(email)


}