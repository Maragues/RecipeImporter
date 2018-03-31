package com.maragues.planner.recipes.hoveringPlanner

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import com.maragues.planner.common.inflate
import com.maragues.planner.common.loadUrl
import com.maragues.planner.common.setVisible
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.hoveringPlanner.HoveringWeekPlannerAdapter.MealViewHolder
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.color
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_hovering_recipe.view.hoveringRecipeImage
import kotlinx.android.synthetic.main.item_hovering_recipe.view.hoveringRecipeTitle
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
    private val replaceRecipeSubject = PublishSubject.create<ReplaceRecipeAction>()

    private val disposables = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val holder = MealViewHolder(parent.inflate(R.layout.item_meal_planner_hover))

        disposables.add(
                holder.recipeAddedObservable()
                        .onTerminateDetach()
                        .subscribe(
                                { mealSlotRecipesSubject.onNext(it) },
                                Throwable::printStackTrace
                        )
        )

        disposables.add(
                holder.recipeReplacedObservable()
                        .onTerminateDetach()
                        .subscribe(
                                { replaceRecipeSubject.onNext(it) },
                                Throwable::printStackTrace
                        )
        )

        return holder
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        disposables.dispose()
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val mealSlot = mealSlots.elementAt(position)

        holder.bind(mealSlot, mealSlotsAndRecipes[mealSlot]!!)
    }

    override fun getItemCount(): Int = mealSlotsAndRecipes.size

    internal fun recipeAddedObservable() = mealSlotRecipesSubject.hide()

    internal fun recipeReplacedObservable() = replaceRecipeSubject.hide()

    internal class MealViewHolder(itemView: View) : ViewHolder(itemView) {

        private val addDragEventListener = PlusZoneDragEventListener()
        private val recipeReplacedSubject = PublishSubject.create<ReplaceRecipeAction>()

        val viewHolderDisposables = CompositeDisposable()

        private lateinit var mealSlot: MealSlot

        init {
            itemView.itemMealAdd.setOnDragListener(addDragEventListener)

            itemView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(p0: View?) {
                    viewHolderDisposables.clear()
                }

                override fun onViewAttachedToWindow(p0: View?) {
                }
            })
        }

        fun bind(mealSlot: MealSlot, recipes: List<Recipe>) {
            this.mealSlot = mealSlot

            determineEmptyVisibility(recipes)

            determineRecipesBackground(mealSlot)

            renderRecipes(recipes)
        }

        private fun determineEmptyVisibility(recipes: List<Recipe>) {
            itemView.itemMealEmptyRecipes.setVisible(recipes.isEmpty())
        }

        private fun determineRecipesBackground(mealSlot: MealSlot) {
            itemView.itemMealRecipeContainer.setBackgroundResource(if (mealSlot.isLunch()) color.weekPlannerLunchBG else color.weekPlannerDinnerBG)
        }

        private fun renderRecipes(recipes: List<Recipe>) {
            val context = itemView.context
            val recipeContainer = itemView.itemMealRecipeContainer as LinearLayout

            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0)
            layoutParams.weight = 1f / recipes.size

            val layoutInflater = LayoutInflater.from(context)

            viewHolderDisposables.clear()

            recipes.forEach {
                layoutInflater.inflate(R.layout.item_hovering_recipe, recipeContainer, true)
                val recipeLayout = recipeContainer.getChildAt(recipeContainer.childCount - 1) as ConstraintLayout
                recipeLayout.hoveringRecipeTitle.text = it.title
                recipeLayout.hoveringRecipeImage.loadUrl(it.url)
                recipeLayout.layoutParams = layoutParams

                val dragListener = RecipeDragEventListener(it.id!!)
                recipeLayout.setOnDragListener(dragListener)

                viewHolderDisposables.add(dragListener.recipeReplacedObservable()
                        .onTerminateDetach()
                        .subscribe(
                                { recipeReplacedSubject.onNext(ReplaceRecipeAction(MealSlotRecipe(mealSlot.date, mealSlot.mealType, it.first), it.second)) },
                                Throwable::printStackTrace
                        )
                )
            }
        }

        internal fun recipeAddedObservable(): Observable<MealSlotRecipe> {
            return addDragEventListener.recipeIdDroppedObservable()
                    .map { MealSlotRecipe(mealSlot.date, mealSlot.mealType, it) }
        }

        internal fun recipeReplacedObservable(): Observable<ReplaceRecipeAction> {
            return recipeReplacedSubject.hide()
        }
    }

    internal data class ReplaceRecipeAction(val mealSlotReplaced: MealSlotRecipe, val newRecipeId: Long)

    internal class RecipeDragEventListener(val recipeId: Long) : OnDragListener {
        private val recipeReplacedSubject = PublishSubject.create<Pair<Long, Long>>()

        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> return true
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.hoveringRecipeImage.setForeground(ColorDrawable(Color.RED))
//                    ImageViewCompat.setImageTintList(v.hoveringRecipeImage, ColorStateList.valueOf(Color.RED))

                    // Invalidate the view to force a redraw in the new tint
                    v.hoveringRecipeImage.invalidate()

                    return true
                }

                DragEvent.ACTION_DRAG_LOCATION -> return true

                DragEvent.ACTION_DRAG_EXITED -> {
                    v.hoveringRecipeImage.setForeground(null)
//                    ImageViewCompat.setImageTintList(v.hoveringRecipeImage, null)

                    // Invalidate the view to force a redraw in the new tint
                    v.hoveringRecipeImage.invalidate()

                    return true
                }

                DragEvent.ACTION_DROP -> {
                    val newRecipeId = event.clipData.getItemAt(0).text.toString().toLong()

                    recipeReplacedSubject.onNext(Pair(recipeId, newRecipeId))

                    return true
                }
                DragEvent.ACTION_DRAG_ENDED -> Timber.d("HoveringAdapter DRAG_EMVDED")
            }

            return false
        }

        internal fun recipeReplacedObservable(): Observable<Pair<Long, Long>> {
            return recipeReplacedSubject
        }
    }

    internal class PlusZoneDragEventListener : OnDragListener {
        private val recipeIdDroppedSubject = PublishSubject.create<Long>()

        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> return true
                DragEvent.ACTION_DRAG_ENTERED -> {
                    Timber.d("HoveringAdapter ACTION_DRAG_ENTERED")

                    // Applies a green tint to the View. Return true the return value is ignored.

                    v.setBackgroundColor(Color.GREEN)

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()

                    return true
                }

                DragEvent.ACTION_DRAG_LOCATION -> return true

                DragEvent.ACTION_DRAG_EXITED -> {
                    Timber.d("HoveringAdapter ACTION_DRAG_EXITED")
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
                DragEvent.ACTION_DRAG_ENDED -> Timber.d("HoveringAdapter DRAG_EMVDED")
            }

            return false
        }

        fun recipeIdDroppedObservable(): Observable<Long> {
            return recipeIdDroppedSubject.hide()
        }
    }
}