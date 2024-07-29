package com.gamerboard.live.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface

/**
 * Created by saurabh.lahoti on 12/08/21
 */
interface DialogClickHandler {
    fun positiveClick(dialog: Dialog) {dialog.dismiss()}
    fun negativeClick(dialog: Dialog) {dialog.dismiss()}
    fun dismiss(dialog: Dialog) {}
}

interface AlertDialogListener {
    fun positiveClick(dialog: DialogInterface) {dialog.dismiss()}
    fun negativeClick(dialog: DialogInterface) {dialog.dismiss()}
}