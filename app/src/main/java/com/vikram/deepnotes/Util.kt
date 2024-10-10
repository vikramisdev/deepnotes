package com.vikram.deepnotes

import android.content.Context
import android.widget.Toast

enum class Theme(val theme: String) {
    LIGHT_THEME("Light Theme"),
    DARK_THEME("Dark Theme"),
    SYSTEM_THEME("System Theme")
}

fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}