package com.maragues.planner.recipes.hoveringPlanner

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.maragues.planner.common.inflate
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.hoveringPlanner.HoveringWeekPlannerAdapter.MealViewHolder
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.color
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_meal_planner_hover.view.itemMealAdd
import kotlinx.android.synthetic.main.item_meal_planner_hover.view.itemMealEmptyRecipes
import kotlinx.android.synthetic.main.item_meal_planner_hover.view.itemMealRecipeContainer
import timber.log.Timber

/*
I need to represent a HoveringMealViewState, that contains a list of recipes

I don't think this is a good approach, since I need to somehow react to the user adding a new recipe to a MealSlot (good name!), be it empty or not

 Should the adapter expose "intentions" as "recipeAddedToMealSlotObservable"? This way we'd delegate the logic to adding the recipe to a MealSlot and
 we'd be responsible for painting a list of recipes

 This'd mean having some kind of LinearLayout with [0-N]Recipes and 1 add. Dropping on recipe recplaces, dropping on Add, inserts
 */
internal class HoveringWeekPlannerAdapter(val mealSlotsAndRecipes: Map<MealSlot, List<Recipe>>) : Adapter<MealViewHolder>() {

    private val mealSlots = mealSlotsAndRecipes.keys

    private val mealSlotRecipesSubject = PublishSubject.create<MealSlotRecipe>()

    private val disposables = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val holder = MealViewHolder(parent.inflate(R.layout.item_meal_planner_hover))

        disposables.add(
                holder.recipeAddedObservable()
                        .subscribe(
                                { mealSlotRecipesSubject.onNext(it) },
                                Throwable::printStackTrace
                        )
        )

        return holder
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)

        disposables.dispose()
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val mealSlot = mealSlots.elementAt(position)

        holder.bind(mealSlot, mealSlotsAndRecipes[mealSlot]!!)
    }

    override fun getItemCount(): Int = mealSlotsAndRecipes.size

    internal fun recipeAddedObservable(): Observable<MealSlotRecipe> {
        return mealSlotRecipesSubject
                .doOnNext({ Timber.d("Emitting mealSlot $it") })
                .hide()
    }

    internal class MealViewHolder(itemView: View) : ViewHolder(itemView) {

        private val addDragEventListener = DragEventListener()

        private lateinit var mealSlot: MealSlot

        init {
            itemView.itemMealAdd.setOnDragListener(addDragEventListener)
        }

        fun bind(mealSlot: MealSlot, recipes: List<Recipe>) {
            this.mealSlot = mealSlot

            if(!recipes.isEmpty())
                Timber.d("")

            determineEmptyVisibility(recipes)

            determineRecipesBackground(mealSlot)

            fillRecipes(recipes)
        }

        private fun determineEmptyVisibility(recipes: List<Recipe>) {
            itemView.itemMealEmptyRecipes.visibility = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        }

        private fun determineRecipesBackground(mealSlot: MealSlot) {
            itemView.itemMealRecipeContainer.setBackgroundResource(if (mealSlot.isLunch()) color.weekPlannerLunchBG else color.weekPlannerDinnerBG)
        }

        private fun fillRecipes(recipes: List<Recipe>) {
            val context = itemView.context
            val recipeContainer = itemView.itemMealRecipeContainer as LinearLayout

            recipes.forEach {
                val textView = TextView(context)
                textView.text = it.title
                recipeContainer.addView(textView)
            }
        }

        internal fun recipeAddedObservable(): Observable<MealSlotRecipe> {
            return addDragEventListener.recipeIdDroppedObservable()
                    .map { MealSlotRecipe(mealSlot.date, mealSlot.mealType, it) }
        }
    }

    internal class DragEventListener : OnDragListener {
        private val recipeIdDroppedSubject = PublishSubject.create<Long>()

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
                    v.setBackgroundResource(R.color.weekPlannerAddBG)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()

                    return true
                }

                DragEvent.ACTION_DROP -> {
                    val recipeId = event.clipData.getItemAt(0).text.toString().toLong()

                    recipeIdDroppedSubject.onNext(recipeId)

                    return true
                }
            }

            return false
        }

        fun recipeIdDroppedObservable(): Observable<Long> {
            return recipeIdDroppedSubject.hide()
        }
    }
}