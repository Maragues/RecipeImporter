package com.maragues.planner.di

import com.maragues.planner.createRecipe.CreateRecipeActivity
import com.maragues.planner.createRecipe.CreateRecipeActivityModule
import com.maragues.planner.home.HomeActivity
import com.maragues.planner.home.HomeActivityModule
import com.maragues.planner.showRecipe.ShowRecipeActivity
import com.maragues.planner.showRecipe.ShowRecipeActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di
 */
@Module
abstract class BindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(HomeActivityModule::class))
    internal abstract fun contributeHomeActivity(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(CreateRecipeActivityModule::class))
    internal abstract fun contributeCreateRecipeFromLinkActivity(): CreateRecipeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(ShowRecipeActivityModule::class))
    internal abstract fun contributeShowRecipeFromLinkActivity(): ShowRecipeActivity

}