package com.hld.networkdisk.client.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.hld.networkdisk.client.R

object DialogFactory {
    fun showAlert(context: Context, title: String = "", message: String, buttonTextResId: Int = R.string.button_ok, onClick: () -> Unit) {
        AlertDialog.Builder(context).setMessage(message).setTitle(title).setPositiveButton(buttonTextResId) { _, _ ->
            onClick()
        }.create().show()
    }

    fun showTwoButtonAlert(
        context: Context,
        title: String = "",
        message: String,
        buttonSureId: Int = R.string.button_sure,
        buttonCancelId: Int = R.string.button_cancel,
        onSureClick: () -> Unit,
        onCancelClick: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context).setMessage(message).setTitle(title).setPositiveButton(buttonSureId) { _, _ ->
            onSureClick()
        }.setNegativeButton(buttonCancelId) { _, _ ->
            onCancelClick?.invoke()
        }.create().show()
    }

    fun showLoadingDialog(context: Context): AlertDialog {
        return AlertDialog.Builder(context).setView(R.layout.dialog_loading_view).create().apply {
            show()
        }
    }

    fun showInputDialog(
        context: Context,

        ) {

    }
}