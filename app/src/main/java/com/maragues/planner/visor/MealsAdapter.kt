package com.maragues.planner.visor

import android.content.ClipData
import android.content.ClipData.Item
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import com.jakewharton.rxrelay2.PublishRelay
import com.maragues.planner.common.inflate
import com.maragues.planner.common.loadUrl
import com.maragues.planner.model.DayMeals
import com.maragues.planner.model.Meal
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.relationships.MealSlotsAndRecipeIds
import com.maragues.planner.recipes.model.MealType
import com.maragues.planner.visor.MealsAdapter.MealDragTouchListener.Companion.localState
import com.maragues.planner.visor.MealsAdapter.MealsViewHolder
import com.maragues.planner_kotlin.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_planner_meal.view.itemPlannerDayDinner
import kotlinx.android.synthetic.main.item_planner_meal.view.itemPlannerDayLunch
import kotlinx.android.synthetic.main.item_planner_meal.view.itemPlannerDinnerHandle
import kotlinx.android.synthetic.main.item_planner_meal.view.itemPlannerLunchHandle
import kotlinx.android.synthetic.main.item_planner_visor_recipe.view.plannerVisorRecipeImage
import kotlinx.android.synthetic.main.item_planner_visor_recipe.view.plannerVisorRecipeTitle
import org.threeten.bp.LocalDate
import timber.log.Timber

internal class MealsAdapter : Adapter<MealsViewHolder>() {
    companion object {
        const val DAYS_IN_WEEK = 7
    }

    private val mealReplacedSubject = PublishRelay.create<MealSlotsAndRecipeIds>()

    private val disposables = CompositeDisposable()

    private var weeksLoaded: Int = 0

    private var list: List<DayMeals> = listOf()

    fun submitList(list: List<DayMeals>) {
        this.list = list

        weeksLoaded = list.size.div(DAYS_IN_WEEK)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): DayMeals {
        if (position == 0) return list.get(0)

        val startIndex = getWeekIndex(position) * DAYS_IN_WEEK

        return list.get(startIndex + getWeekOffset(position))
    }

    fun getWeekIndex(position: Int) = position % weeksLoaded

    fun getWeekOffset(position: Int) = position / weeksLoaded

    fun getTotalWeeks() = weeksLoaded

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder {
        val mealsViewHolder = MealsViewHolder(parent.inflate(R.layout.item_planner_meal))

        disposables.add(mealsViewHolder.lunchReplacedObservable()
                .onTerminateDetach()
                .subscribe(
                        mealReplacedSubject::accept,
                        Throwable::printStackTrace
                ))

        disposables.add(mealsViewHolder.dinnerReplacedObservable()
                .onTerminateDetach()
                .subscribe(
                        mealReplacedSubject::accept,
                        Throwable::printStackTrace
                ))

        return mealsViewHolder
    }

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) = holder.bind(getItem(position), columnWidth, columnHeight)

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        disposables.dispose()
    }

    private var columnWidth: Int? = null
    fun setColumnWidth(columnWidth: Int) {
        this.columnWidth = columnWidth
    }

    private var columnHeight: Int? = null
    fun setColumnHeight(columnHeight: Int) {
        this.columnHeight = columnHeight
    }

    internal fun mealReplacedObservable() = mealReplacedSubject.hide()

    class MealsViewHolder(itemView: View) : ViewHolder(itemView) {

        private val lunchDragTouchListener = MealDragTouchListener()
        private val dinnerDragTouchListener = MealDragTouchListener()
        private val dinnerDragEventListener = MealDragEventListener()
        private val lunchDragEventListener = MealDragEventListener()

        fun lunchReplacedObservable(): Observable<MealSlotsAndRecipeIds> {
            return lunchDragEventListener.recipeReplacedObservable()
        }

        fun dinnerReplacedObservable(): Observable<MealSlotsAndRecipeIds> {
            return dinnerDragEventListener.recipeReplacedObservable()
        }

        fun bind(dayMeals: DayMeals, columnWidth: Int?, columnHeight: Int?) {
            renderRecipes(itemView.itemPlannerDayLunch, dayMeals.lunch.recipes, dayMeals.date)
            renderRecipes(itemView.itemPlannerDayDinner, dayMeals.dinner.recipes, dayMeals.date)

            setupDragAndDrop(dayMeals)

            maybeAdjustSize(columnWidth, columnHeight)
        }

        private fun maybeAdjustSize(columnWidth: Int?, columnHeight: Int?) {
            if (columnWidth != null && columnWidth != itemView.layoutParams.width) itemView.layoutParams.width = columnWidth
            if (columnHeight != null && columnHeight != itemView.layoutParams.height) itemView.layoutParams.height = columnHeight
        }

        private fun renderRecipes(recipeContainer: LinearLayout, recipes: List<Recipe>, date: LocalDate) {
            val layoutInflater = LayoutInflater.from(recipeContainer.context)

            val layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0)
            layoutParams.weight = 1f / recipes.size

            recipeContainer.removeAllViews()

            recipes.forEach {
                Timber.d("Rendering ${it.title} for $date")
                layoutInflater.inflate(R.layout.item_planner_visor_recipe, recipeContainer, true)
                val recipeLayout = recipeContainer.getChildAt(recipeContainer.childCount - 1) as ConstraintLayout
                recipeLayout.plannerVisorRecipeTitle.text = it.title
                recipeLayout.plannerVisorRecipeImage.loadUrl(it.screenshot)
                recipeLayout.layoutParams = layoutParams
            }
        }

        private fun setupDragAndDrop(dayMeals: DayMeals) {
            setupDrag(dayMeals)

            setupDrop(dayMeals)
        }

        private fun setupDrop(dayMeals: DayMeals) {
            lunchDragEventListener.setMeal(dayMeals.lunch)
            dinnerDragEventListener.setMeal(dayMeals.dinner)

            itemView.itemPlannerDayLunch.setOnDragListener(lunchDragEventListener)
            itemView.itemPlannerDayDinner.setOnDragListener(dinnerDragEventListener)
        }

        private fun setupDrag(dayMeals: DayMeals) {
            lunchDragTouchListener.meal = dayMeals.lunch
            dinnerDragTouchListener.meal = dayMeals.dinner

            lunchDragTouchListener.shadow = itemView.itemPlannerDayLunch
            dinnerDragTouchListener.shadow = itemView.itemPlannerDayDinner

            itemView.itemPlannerLunchHandle.setOnTouchListener(lunchDragTouchListener)
            itemView.itemPlannerDinnerHandle.setOnTouchListener(dinnerDragTouchListener)
        }
    }

    class MealDragTouchListener(var meal: Meal? = null, var shadow: View? = null) : OnTouchListener {
        companion object {
            fun localState(date: LocalDate, mealType: MealType): Int {
                return date.toEpochDay().toInt() + mealType.hashCode()
            }
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (meal?.recipes!!.isEmpty()) return false

                    val clipData = clipDataWithRecipeIds()

                    val shadow = View.DragShadowBuilder(shadow)

                    val localState = localState(meal!!.date, meal!!.mealType)
                    if (VERSION.SDK_INT >= VERSION_CODES.N) {
                        v.startDragAndDrop(clipData, shadow, localState, 0)
                    } else {
                        @Suppress("DEPRECATION")
                        v.startDrag(clipData, shadow, localState, 0)
                    }

                    return true
                }
            }

            return false
        }

        private fun clipDataWithRecipeIds(): ClipData {
            val recipes = meal!!.recipes
            val clipData = ClipData.newPlainText("recipes", recipes.get(0).id.toString())
            recipes.subList(1, recipes.size).forEach {
                clipData.addItem(Item(it.id.toString()))
            }

            return clipData
        }
    }

    internal class MealDragEventListener(var date: LocalDate? = null, var mealType: MealType? = null) : OnDragListener {
        private val mealReplacedSubject = PublishRelay.create<MealSlotsAndRecipeIds>()

        fun setMeal(meal: Meal) {
            date = meal.date
            mealType = meal.mealType
        }

        override fun onDrag(v: View, event: DragEvent): Boolean {
            if (date == null || mealType == null) return false

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    val localState = localState(date!!, mealType!!)

                    //only accept further drag events if it's not the same view
                    val accept = localState != event.localState

                    if (!accept) Timber.d("Rejected " + date + "-" + mealType + " with localState " + localState + " vs incoming state " + event.localState)

                    return accept
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.setForeground(ColorDrawable(Color.RED))

                    return true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    v.setForeground(null)

                    return true
                }

                DragEvent.ACTION_DROP -> {
                    val recipes = mutableListOf<Long>()

                    for (i in 0 until event.clipData.itemCount) {
                        recipes.add(event.clipData.getItemAt(i).text.toString().toLong())
                    }

                    mealReplacedSubject.accept(MealSlotsAndRecipeIds(date!!, mealType!!, recipes))

                    return true
                }
            }

            return false
        }

        internal fun recipeReplacedObservable(): Observable<MealSlotsAndRecipeIds> {
            return mealReplacedSubject
        }
    }
}