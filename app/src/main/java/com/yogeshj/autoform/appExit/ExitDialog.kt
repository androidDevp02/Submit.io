package com.yogeshj.autoform.appExit

import android.app.Activity
import android.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import kotlin.system.exitProcess

object ExitDialog {

    fun exit(context:Activity){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Do you really want to exit?")

        builder.setPositiveButton("Yes") { _, _ ->
            context.finish()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

}