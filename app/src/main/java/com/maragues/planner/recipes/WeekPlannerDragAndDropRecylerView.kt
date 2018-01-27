package com.maragues.planner.recipes

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.DragEvent.ACTION_DRAG_ENDED
import android.view.DragEvent.ACTION_DRAG_ENTERED
import android.view.DragEvent.ACTION_DRAG_EXITED
import android.view.DragEvent.ACTION_DRAG_LOCATION
import android.view.DragEvent.ACTION_DRAG_STARTED
import android.view.DragEvent.ACTION_DROP
import android.view.View
import android.view.ViewGroup
import com.maragues.planner.common.inflate
import com.maragues.planner.recipes.WeekPlannerDragAndDropRecylerView.WeekPlannerAdapter.MealViewHolder
import com.maragues.planner.ui.recyclerView.RowsAndColumnsItemDecoration
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.dimen

/**
 * RecylerView that becomes visible when a user starts a drag and goes invisible some time after the
 * drag is completed
 *
 * Created by miguelaragues on 27/1/18.
 */
class WeekPlannerDragAndDropRecylerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {
    companion object {
        const val COLUMNS = 2
    }

    init {
        layoutManager = GridLayoutManager(context, COLUMNS)
        decorateRecyclerView()
        adapter = WeekPlannerAdapter()
    }

    private fun decorateRecyclerView() {
        val rowSpacing = resources.getDimensionPixelSize(dimen.recipe_planner_grid_spacing_row)
        val columnSpacing = resources.getDimensionPixelSize(dimen.recipe_planner_grid_spacing_column)
        addItemDecoration(RowsAndColumnsItemDecoration(rowSpacing, columnSpacing, COLUMNS))
    }

    override fun onDragEvent(event: DragEvent): Boolean {
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                alpha = 1f

                return true
            }

            ACTION_DRAG_ENDED -> postDelayed({
                alpha = 0f
            }, 500)
        }

        return false
    }

    private class WeekPlannerAdapter : RecyclerView.Adapter<MealViewHolder>() {
        val meals: List<Meal> = listOf(Meal(), Meal(), Meal(), Meal())

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder =
                MealViewHolder(parent.inflate(R.layout.item_meal_planner_hover))

        override fun onBindViewHolder(holder: MealViewHolder, position: Int) = holder.bind(meals[position])

        override fun getItemCount(): Int = meals.size

        class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnDragListener(DragEventListener())
            }

            fun bind(meal: Meal) {

            }
        }

        class DragEventListener : OnDragListener {
            override fun onDrag(v: View, event: DragEvent): Boolean {
                when (event.action) {
                    ACTION_DRAG_STARTED -> return true
                    ACTION_DRAG_ENTERED -> {

                        // Applies a green tint to the View. Return true the return value is ignored.

                        v.setBackgroundColor(Color.GREEN)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        return true
                    }

                    ACTION_DRAG_LOCATION -> return true

                    ACTION_DRAG_EXITED -> {

                        // Re-sets the color tint to blue. Returns true the return value is ignored.
                        v.setBackgroundColor(Color.BLUE)

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate()

                        return true
                    }

                    ACTION_DROP -> {
                        v.setBackgroundColor(Color.GRAY)

                        v.invalidate()

                        return true
                    }
                }

                return false
            }
        }

        class Meal
    }
}