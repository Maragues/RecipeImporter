package com.maragues.planner.recipes

import android.graphics.Color
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import com.maragues.planner.common.inflate
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.MealSlot
import com.maragues.planner.recipes.HoverWeekPlannerAdapter.MealViewHolder
import com.maragues.planner_kotlin.R.layout

/*
I need to represent a HoveringMealViewState, that contains a list of recipes

I don't think this is a good approach, since I need to somehow react to the user adding a new recipe to a MealSlot (good name!), be it empty or not

 Should the adapter expose "intentions" as "recipeAddedToMealSlotObservable"? This way we'd delegate the logic to adding the recipe to a MealSlot and
 we'd be responsible for painting a list of recipes

 This'd mean having some kind of LinearLayout with [0-N]Recipes and 1 add. Dropping on recipe recplaces, dropping on Add, inserts
 */
internal class HoverWeekPlannerAdapter(val mealSlotsAndRecipes: Map<MealSlot, List<Recipe>>) : Adapter<MealViewHolder>() {

    val mealSlots = mealSlotsAndRecipes.keys

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder =
            MealViewHolder(parent.inflate(layout.item_meal_planner_hover))

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val mealSlot = mealSlots.elementAt(position)

        holder.bind(mealSlot, mealSlotsAndRecipes[mealSlot]!!)
    }

    override fun getItemCount(): Int = mealSlotsAndRecipes.size

    class MealViewHolder(itemView: View) : ViewHolder(itemView) {
        init {
            itemView.setOnDragListener(DragEventListener())
        }

        fun bind(mealSlot: MealSlot, recipes: List<Recipe>) {

        }
    }

    class DragEventListener : OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> return true
                DragEvent.ACTION_DRAG_ENTERED -> {

                    // Applies a green tint to the View. Return true the return value is ignored.

                    v.setBackgroundColor(Color.GREEN)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()

                    return true
                }

                DragEvent.ACTION_DRAG_LOCATION -> return true

                DragEvent.ACTION_DRAG_EXITED -> {

                    // Re-sets the color tint to blue. Returns true the return value is ignored.
                    v.setBackgroundColor(Color.BLUE)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()

                    return true
                }

                DragEvent.ACTION_DROP -> {
                    v.setBackgroundColor(Color.GRAY)

                    v.invalidate()

                    return true
                }
            }

            return false
        }
    }
}