package com.ester.githubuser.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.ester.githubuser.R

class UserNotFoundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val tvErrorMessage: TextView
    fun setErrorMessage(errorMessage: String?) {
        tvErrorMessage.text = errorMessage
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_not_found, this)
        tvErrorMessage = findViewById(R.id.tv_error_message)
    }
}