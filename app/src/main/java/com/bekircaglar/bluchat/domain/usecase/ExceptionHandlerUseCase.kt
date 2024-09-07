package com.bekircaglar.bluchat.domain.usecase

import com.bekircaglar.bluchat.Response
import javax.inject.Inject

class ExceptionHandlerUseCase @Inject constructor() {

    operator fun invoke(e: Exception):String{
        return Response.Error(e.message.toString()).message
    }
}