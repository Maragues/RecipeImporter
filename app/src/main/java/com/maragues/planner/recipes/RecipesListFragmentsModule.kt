package com.maragues.planner.recipes

import com.maragues.planner.di.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by miguelaragues on 11/2/18.
 */
@Module
abstract class RecipesListFragmentsModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [HoveringPlannerFragmentModule::class])
    internal abstract fun contributeHoveringPlannerFragment(): HoveringPlannerFragment
}