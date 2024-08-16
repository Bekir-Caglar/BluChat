package com.bekircaglar.chatappbordo

import android.app.AlertDialog
import android.content.Context


class ShowErrorDialog(){
    fun showErrorDialog(context: Context, errorMessage: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Hata")
        builder.setMessage(errorMessage)
        builder.setPositiveButton("Tamam") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
