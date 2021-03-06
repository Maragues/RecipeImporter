package com.maragues.planner.home

import com.maragues.planner.recipes.RecipesListFragment
import com.maragues.planner.recipes.RecipesListModule
import com.maragues.planner.visor.PlannerVisorFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [HomeFragmentsModule::class])
abstract class HomeActivityModule {

    @Module
    companion object {
    }
}

@Module
abstract class HomeFragmentsModule {
    @ContributesAndroidInjector(modules = arrayOf(RecipesListModule::class))
    internal abstract fun contributeRecipesListFragment(): RecipesListFragment

    @ContributesAndroidInjector
    internal abstract fun contributePlannerVisorFragment(): PlannerVisorFragment
}
