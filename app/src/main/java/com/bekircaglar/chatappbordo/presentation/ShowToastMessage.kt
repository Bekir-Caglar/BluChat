package com.bekircaglar.chatappbordo.presentation

import android.content.Context
import android.widget.Toast

fun ShowToastMessage(context:Context,message:String){

    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
}