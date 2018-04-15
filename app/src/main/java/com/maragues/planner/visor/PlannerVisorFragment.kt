package com.maragues.planner.visor

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.HORIZONTAL
import android.support.v7.widget.RecyclerView.ItemDecoration
import android.support.v7.widget.RecyclerView.OnItemTouchListener
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.support.v7.widget.RecyclerView.State
import android.support.v7.widget.RecyclerView.VERTICAL
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import com.maragues.planner.common.BaseFragment
import com.maragues.planner.common.inflate
import com.maragues.planner.visor.PlannerVisorFragment.DaysAdapter.DayViewHolder
import com.maragues.planner.visor.PlannerVisorFragment.WeeksAdapter.WeekViewHolder
import com.maragues.planner.visor.PlannerVisorViewModel.Factory
import com.maragues.planner_kotlin.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_planner_visor.plannerDaysRecyclerView
import kotlinx.android.synthetic.main.fragment_planner_visor.plannerDragBottomZone
import kotlinx.android.synthetic.main.fragment_planner_visor.plannerDragRightZone
import kotlinx.android.synthetic.main.fragment_planner_visor.plannerMealsRecyclerView
import kotlinx.android.synthetic.main.fragment_planner_visor.plannerWeeksRecyclerView
import kotlinx.android.synthetic.main.item_planner_day.view.itemPlannerDay
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue

class PlannerVisorFragment : BaseFragment() {

    @Inject
    internal lateinit var viewModelFactory: Factory

    private lateinit var viewModel: PlannerVisorViewModel

    private val mealsAdapter = MealsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_planner_visor, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PlannerVisorViewModel::class.java)

        initDaysRecyclerView()

        initWeeksRecyclerView()

        initMealsRecyclerView()

        synchronizeRecyclerViews()
    }

    private fun initDaysRecyclerView() {
        val daysLayoutManager = LinearLayoutManager(context, VERTICAL, false)

        plannerDaysRecyclerView.layoutManager = daysLayoutManager
        plannerDaysRecyclerView.adapter = DaysAdapter()

        plannerDaysRecyclerView.addOnItemTouchListener(scrollDisabler)
        plannerDaysRecyclerView.addItemDecoration(object : ItemDecoration() {
            val separation: Int = context!!.resources.getDimensionPixelSize(R.dimen.visor_meals_separation)

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                val position = parent.getChildLayoutPosition(view)

                outRect.top = if (position == 0) 0 else separation
            }
        })
    }

    private fun initWeeksRecyclerView() {
        val weeksLayoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        plannerWeeksRecyclerView.layoutManager = weeksLayoutManager
        plannerWeeksRecyclerView.adapter = WeeksAdapter()

        plannerWeeksRecyclerView.addOnItemTouchListener(scrollDisabler)
        plannerWeeksRecyclerView.addItemDecoration(object : ItemDecoration() {
            val separation: Int = context!!.resources.getDimensionPixelSize(R.dimen.visor_meals_separation)

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                val position = parent.getChildLayoutPosition(view)

                outRect.left = if (position == 0) 0 else separation
                outRect.right = if (position == (parent.adapter?.itemCount?.minus(1))) 0 else separation
            }
        })
    }

    private fun initMealsRecyclerView() {
        val mealsLayoutManager = FixedGridLayoutManager()
        mealsLayoutManager.setTotalColumnCount(3)
        plannerMealsRecyclerView.layoutManager = mealsLayoutManager

        plannerMealsRecyclerView.adapter = mealsAdapter
        plannerMealsRecyclerView.addOnScrollListener(mealsScrollListener)

        viewModel.onMealReplacedObservable(mealsAdapter.mealReplacedObservable())

        disposables().add(plannerMealsRecyclerView.columnHeightObservable()
                .onTerminateDetach()
                .subscribe(
                        this::onColumnHeightReceived,
                        Throwable::printStackTrace
                )
        )

        disposables().add(plannerMealsRecyclerView.columnWidthObservable()
                .onTerminateDetach()
                .subscribe(
                        this::onColumnWidthReceived,
                        Throwable::printStackTrace
                )
        )

        initDragZones()
    }

    private fun initDragZones() {
        plannerDaysRecyclerView.setOnDragListener(object : OnDragListener {
            private var yEntered: Float = 0f
            private val xMultiplier = 5

            override fun onDrag(v: View, event: DragEvent): Boolean {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        yEntered = event.y

                        return true
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> return true
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        val dX = (v.width - event.x).absoluteValue
                        val dY = event.y - yEntered

                        /*
                        I tried to set an interval + invoking smoothScrollBy and it didn't work.

                        I also tried to queue it through a handler and it also didn't work
                         */
                        plannerMealsRecyclerView.smoothScrollBy(-(dX.toInt() * xMultiplier), dY.toInt())
                    }
                }
                return false
            }
        })

        plannerDragRightZone.setOnDragListener(object : OnDragListener {
            private var yEntered: Float = 0f

            override fun onDrag(v: View, event: DragEvent): Boolean {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        yEntered = event.y

                        return true
                    }
                    DragEvent.ACTION_DRAG_ENTERED -> return true
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        val dY = event.y - yEntered

                        plannerMealsRecyclerView.smoothScrollBy(event.x.toInt(), dY.toInt())
                    }
                }
                return false
            }
        })

        plannerWeeksRecyclerView.setOnDragListener(OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED, DragEvent.ACTION_DRAG_ENTERED -> return@OnDragListener true
                DragEvent.ACTION_DRAG_LOCATION -> {
                    plannerMealsRecyclerView.smoothScrollBy(0, -(v.height*3))
                }
            }
            false
        })

        plannerDragBottomZone.setOnDragListener(OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED, DragEvent.ACTION_DRAG_ENTERED -> return@OnDragListener true
                DragEvent.ACTION_DRAG_LOCATION -> {
                    plannerMealsRecyclerView.smoothScrollBy(0, v.height)
                }
            }
            false
        })
    }

    override fun onResume() {
        super.onResume()

        disposables().add(viewModel.viewStateObservable()
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { render(it) },
                        Throwable::printStackTrace
                ))
    }

    override fun onPause() {
        super.onPause()

        disposables().clear()
    }

    private fun render(viewState: PlannerVisorViewState) {
        mealsAdapter.submitList(viewState.dayMeals.sortedDescending())
    }

    private fun onColumnHeightReceived(columnHeight: Int) {
        (plannerMealsRecyclerView.adapter as MealsAdapter).setColumnHeight(columnHeight)
        (plannerDaysRecyclerView.adapter as DaysAdapter).setColumnHeight(columnHeight)
    }

    private fun onColumnWidthReceived(columnWidth: Int) {
        (plannerMealsRecyclerView.adapter as MealsAdapter).setColumnWidth(columnWidth)
        (plannerWeeksRecyclerView.adapter as WeeksAdapter).setColumnWidth(columnWidth)
    }

    private fun synchronizeRecyclerViews() {
    }

    val mealsScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            plannerWeeksRecyclerView.scrollBy(dx, 0)
            plannerDaysRecyclerView.scrollBy(0, dy)
        }
    }

    val scrollDisabler = object : OnItemTouchListener {
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }
    }

    private class DaysAdapter : RecyclerView.Adapter<DayViewHolder>() {

        val weekDays: MutableList<String> = mutableListOf()

        init {
            initWeekdays()
        }

        private fun initWeekdays() {
            val now = LocalDate.now()
            val fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek()
            val weekdayFormatter = DateTimeFormatter.ofPattern("EEE")

            for (i in 1..7) {
                weekDays.add(weekdayFormatter.format(now.with(fieldISO, i.toLong())))
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
            return DayViewHolder(parent.inflate(R.layout.item_planner_day))
        }

        override fun getItemCount() = weekDays.size

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) = holder.bind(weekDays.get(position), columnHeight)

        var columnHeight: Int? = null
        fun setColumnHeight(columnHeight: Int) {
            this.columnHeight = columnHeight
        }

        class DayViewHolder(itemView: View) : ViewHolder(itemView) {
            fun bind(day: String, columnHeight: Int?) {
                itemView.itemPlannerDay.text = day

                if (columnHeight != null) itemView.layoutParams.height = columnHeight
            }

        }
    }

    private class WeeksAdapter : RecyclerView.Adapter<WeekViewHolder>() {

        val weeks: MutableList<String> = mutableListOf("Previous Week", "Current Week", "Next Week")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
            return WeekViewHolder(parent.inflate(R.layout.item_planner_day))
        }

        override fun getItemCount() = weeks.size

        override fun onBindViewHolder(holder: WeekViewHolder, position: Int) = holder.bind(weeks.get(position), columnWidth)

        var columnWidth: Int? = null
        fun setColumnWidth(columnWidth: Int) {
            this.columnWidth = columnWidth
        }

        class WeekViewHolder(itemView: View) : ViewHolder(itemView) {
            fun bind(day: String, columnWidth: Int?) {
                itemView.itemPlannerDay.text = day

                if (columnWidth != null) itemView.layoutParams.width = columnWidth
            }

        }
    }
}