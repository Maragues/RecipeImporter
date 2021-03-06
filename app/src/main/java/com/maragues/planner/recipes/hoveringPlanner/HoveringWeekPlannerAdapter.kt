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
import com.maragues.planner.recipes.hoveringPlanner.HoveringPlannerFragmentViewModel.Companion.DAYS_DISPLAYED
import com.maragues.planner.recipes.hoveringPlanner.HoveringPlannerRecyclerView.Companion.COLUMNS
import com.maragues.planner.recipes.hoveringPlanner.HoveringWeekPlannerAdapter.MealViewHolder
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.color
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_header_planner_hover.view.itemHoverHeaderText
import kotlinx.android.synthetic.main.item_hovering_recipe.view.hoveringRecipeImage
import kotlinx.android.synthetic.main.item_hovering_recipe.view.hoveringRecipeTitle
import kotlinx.android.synthetic.main.item_meal_planner_hover.view.itemMealAdd
import kotlinx.android.synthetic.main.item_meal_planner_hover.view.itemMealEmptyRecipes
import kotlinx.android.synthetic.main.item_meal_planner_hover.view.itemMealRecipeContainer
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

internal class HoveringWeekPlannerAdapter(val mealSlotsAndRecipes: Map<MealSlot, List<Recipe>>) : Adapter<ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_CELL = 2

        const val MEAL_TYPES = 2
    }

    private val mealSlots = mealSlotsAndRecipes.keys

    private val mealSlotRecipesSubject = PublishSubject.create<MealSlotRecipe>()
    private val replaceRecipeSubject = PublishSubject.create<ReplaceRecipeAction>()

    private val disposables = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> createHeaderViewHolder(parent)
            else -> createRecipeSlotViewHolder(parent)
        }
    }

    private fun createRecipeSlotViewHolder(parent: ViewGroup): MealViewHolder {
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

    private fun createHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        return HeaderViewHolder(parent.inflate(R.layout.item_header_planner_hover))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        disposables.dispose()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mealSlot = getItem(position)
        if (holder is MealViewHolder) {
            holder.bind(mealSlot, mealSlotsAndRecipes[mealSlot]!!)
        } else if (holder is HeaderViewHolder) {
            holder.bind(mealSlot)
        }
    }

    private fun getItem(position: Int): MealSlot {
        val row = position / COLUMNS

        //return a mealSlot corresponding to that row so that we can read the LocalDate
        if (getItemViewType(position) == TYPE_HEADER) return mealSlots.elementAt(row * 2 + 1)

        return mealSlots.elementAt(position - row - 1)
    }

    override fun getItemViewType(position: Int) = if (position % COLUMNS == 0) TYPE_HEADER else TYPE_CELL

    override fun getItemCount(): Int = mealSlotsAndRecipes.size + DAYS_DISPLAYED.toInt()

    internal fun recipeAddedObservable() = mealSlotRecipesSubject.hide()

    internal fun recipeReplacedObservable() = replaceRecipeSubject.hide()

    internal class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {

        companion object {
            private val WEEKDAY_FORMATTER = DateTimeFormatter.ofPattern("EEE")
        }

        fun bind(mealSlot: MealSlot) {
            itemView.itemHoverHeaderText.text = WEEKDAY_FORMATTER.format(mealSlot.date)
        }
    }

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
                recipeLayout.hoveringRecipeImage.loadUrl(it.screenshot)
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