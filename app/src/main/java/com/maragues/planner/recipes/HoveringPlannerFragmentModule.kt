package com.maragues.planner.recipes

import com.maragues.planner.di.FragmentScope
import com.maragues.planner.persistence.repositories.MealSlotRepository
import dagger.Module
import dagger.Provides

/**
 * Created by miguelaragues on 11/2/18.
 */
@Module
class HoveringPlannerFragmentModule {

    @Module
    @FragmentScope
    companion object {

        @JvmStatic
        @Provides
        fun providesViewModelFactory(mealSlotRepository: MealSlotRepository): HoveringPlannerFragmentViewModel.Factory {
            return HoveringPlannerFragmentViewModel.Factory(mealSlotRepository)
        }
    }
}