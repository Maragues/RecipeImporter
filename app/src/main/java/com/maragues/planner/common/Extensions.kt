package com.maragues.planner.common

import android.support.annotation.ColorRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.maragues.planner_kotlin.R
import com.squareup.picasso.Picasso

/**
 * Created by miguelaragues on 6/1/18.
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun ImageView.loadUrl(url: String?,
                      @ColorRes colorPlaceHolder: Int = R.color.weekPlannerLunchBG) {
    Picasso.get()
            .load(sanitizeUrl(url))
            .placeholder(colorPlaceHolder)
            .into(this)
}

private fun sanitizeUrl(url: String?) = if (url == null || url.isEmpty()) null else url

fun TextView.setTextIfEmpty(newText: String) {
    if (text.isEmpty())
        text = newText
}

fun View.setVisible(visible: Boolean) {
    val visibility = if (visible) View.VISIBLE else View.GONE

    setVisibility(visibility)
}