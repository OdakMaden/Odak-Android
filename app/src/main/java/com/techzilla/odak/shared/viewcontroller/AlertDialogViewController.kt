package com.techzilla.odak.shared.viewcontroller

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogViewController  {

    companion object {
        fun buildAlertDialog(
            context: Context,
            title: String,
            subTitle: String,
            buttonCancelTitle: String,
            buttonNegativeTitle: String,
            buttonPositiveTitle: String
        ) {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(subTitle)
                .setNeutralButton(buttonCancelTitle) { dialog, which ->

                }
                .setNegativeButton(buttonNegativeTitle) { dialog, which ->

                }
                .setPositiveButton(buttonPositiveTitle) { dialog, which ->

                }
                .show()
        }
    }
}