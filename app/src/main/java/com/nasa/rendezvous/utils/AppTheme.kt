package com.nasa.rendezvous.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

object AppTheme {
    fun setupInsets(toolbar: Toolbar, recyclerView: RecyclerView, parentId: View, context: Context) {
        val baseMoviesPadding = pxFromDp(10f, context)
        val toolbarHeight = toolbar.layoutParams.height
        parentId.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { _, insets ->
            toolbar.setMarginTop(insets.systemWindowInsetTop)
            recyclerView.updatePadding(top = toolbarHeight + insets.systemWindowInsetTop + baseMoviesPadding)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { _, insets ->
            recyclerView.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun View.setMarginTop(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = value
    }

    private fun pxFromDp(dp: Float, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}