package com.maragues.planner.visor

import android.support.constraint.ConstraintLayout
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil.ItemCallback
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import com.maragues.planner.common.inflate
import com.maragues.planner.common.loadUrl
import com.maragues.planner.model.DayMeals
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.visor.MealsAdapter.MealsViewHolder
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.layout
import kotlinx.android.synthetic.main.item_planner_meal.view.itemPlannerDayDinner
import kotlinx.android.synthetic.main.item_planner_meal.view.itemPlannerDayLunch
import kotlinx.android.synthetic.main.item_planner_visor_recipe.view.plannerVisorRecipeImage
import kotlinx.android.synthetic.main.item_planner_visor_recipe.view.plannerVisorRecipeTitle
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

internal class MealsAdapter : ListAdapter<DayMeals, MealsViewHolder>(MealsDiffCallback()) {
    companion object {
        const val DAYS_IN_WEEK = 7
    }

    private var weeksLoaded: Int = 0

    override fun submitList(list: List<DayMeals>?) {
        super.submitList(list)

        weeksLoaded = list?.size?.div(DAYS_IN_WEEK) ?: 0
    }

    override fun getItem(position: Int): DayMeals {
        if (position == 0) return super.getItem(position)

        val startIndex = getWeekIndex(position) * DAYS_IN_WEEK

        return super.getItem(startIndex + getWeekOffset(position))
    }

    fun getWeekIndex(position: Int) = position % weeksLoaded

    fun getWeekOffset(position: Int) = position / weeksLoaded

    fun getTotalWeeks() = weeksLoaded

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder {
        return MealsViewHolder(parent.inflate(R.layout.item_planner_meal))
    }

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) = holder.bind(getItem(position), columnWidth, columnHeight)

    var columnWidth: Int? = null
    fun setColumnWidth(columnWidth: Int) {
        this.columnWidth = columnWidth
    }

    var columnHeight: Int? = null
    fun setColumnHeight(columnHeight: Int) {
        this.columnHeight = columnHeight
    }

    class MealsViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(dayMeals: DayMeals, columnWidth: Int?, columnHeight: Int?) {
            renderRecipes(itemView.itemPlannerDayLunch, dayMeals.lunch.recipes, dayMeals.date)
            renderRecipes(itemView.itemPlannerDayDinner, dayMeals.dinner.recipes, dayMeals.date)

            if (columnWidth != null && columnWidth != itemView.layoutParams.width) itemView.layoutParams.width = columnWidth
            if (columnHeight != null && columnHeight != itemView.layoutParams.height) itemView.layoutParams.height = columnHeight
        }

        private fun renderRecipes(recipeContainer: LinearLayout, recipes: List<Recipe>, date: LocalDate) {
            val layoutInflater = LayoutInflater.from(recipeContainer.context)

            val layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0)
            layoutParams.weight = 1f / recipes.size

            recipeContainer.removeAllViews()

            Timber.d("Rendering ${recipes.size} for $date")

            recipes.forEach {
                Timber.d("Rendering ${it.title} for $date")
                layoutInflater.inflate(R.layout.item_planner_visor_recipe, recipeContainer, true)
                val recipeLayout = recipeContainer.getChildAt(recipeContainer.childCount - 1) as ConstraintLayout
                recipeLayout.plannerVisorRecipeTitle.text = it.title
                recipeLayout.plannerVisorRecipeImage.loadUrl(it.screenshot)
                recipeLayout.layoutParams = layoutParams

//                    val dragListener = RecipeDragEventListener(it.id!!)
//                    recipeLayout.setOnDragListener(dragListener)
            }
        }
    }

    class MealsDiffCallback : ItemCallback<DayMeals>() {
        override fun areItemsTheSame(oldItem: DayMeals, newItem: DayMeals): Boolean {
            return areContentsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: DayMeals, newItem: DayMeals): Boolean {
            return oldItem == newItem
        }
    }
}