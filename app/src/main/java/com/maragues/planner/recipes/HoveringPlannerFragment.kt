package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maragues.planner.common.BaseFragment
import com.maragues.planner_kotlin.R
import kotlinx.android.synthetic.main.fragment_hovering_planner.weekMenuDragAndDrop
import javax.inject.Inject

/**
 * Created by miguelaragues on 11/2/18.
 */
class HoveringPlannerFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: HoveringPlannerFragmentViewModel.Factory

    private lateinit var viewModel: HoveringPlannerFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hovering_planner, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HoveringPlannerFragmentViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        disposables().add(viewModel.viewStateObservable()
                .subscribe(
                        { render(it) },
                        Throwable::printStackTrace
                ))
    }

    override fun onPause() {
        super.onPause()

        disposables().clear()
    }

    private fun render(viewState: HoveringPlannerViewState) {
        val hoveringAdapter = HoverWeekPlannerAdapter(viewState.meals)
        weekMenuDragAndDrop.adapter = hoveringAdapter
    }
}