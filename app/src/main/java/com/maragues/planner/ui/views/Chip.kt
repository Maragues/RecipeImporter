package com.maragues.planner.ui.views

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.maragues.planner_kotlin.R

/**
 * Created by miguelaragues on 25/2/18.
 */
open class Chip constructor(context: Context) : TextView(context) {

    init {
        this.setBackgroundResource(R.drawable.shape_chip_bakground)
        gravity = Gravity.CENTER
    }

    fun setChipBackground(color: Int) {
        val background = background
    }
}