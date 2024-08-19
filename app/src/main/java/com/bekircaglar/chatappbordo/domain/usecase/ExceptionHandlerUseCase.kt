package com.bekircaglar.chatappbordo.domain.usecase

import com.bekircaglar.chatappbordo.Response
import javax.inject.Inject

class ExceptionHandlerUseCase @Inject constructor() {

    operator fun invoke(e: Exception):String{
        return Response.Error(e.message.toString()).message
    }
}