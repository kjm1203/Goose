package com.ssafy.firstproject.util

import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.ssafy.firstproject.R

object ViewUtil {
    fun setProgressDrawableByTarget(progressBar: ProgressBar, targetProgress: Int) {
        val context = progressBar.context
        val drawableRes = when {
            targetProgress < 33 -> R.drawable.bg_progress_bar_33
            targetProgress < 66 -> R.drawable.bg_progress_bar_66
            else -> R.drawable.bg_progress_bar_100
        }
        progressBar.progressDrawable = ContextCompat.getDrawable(context, drawableRes)
    }
}