package com.maragues.planner.di

import com.maragues.planner.createRecipe.CreateRecipeActivity
import com.maragues.planner.createRecipe.CreateRecipeActivityModule
import com.maragues.planner.recipes.RecipesListActivity
import com.maragues.planner.recipes.RecipesListModule
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
    @ContributesAndroidInjector(modules = arrayOf(CreateRecipeActivityModule::class))
    internal abstract fun contributeNewRecipeFromLinkActivity(): CreateRecipeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(ShowRecipeActivityModule::class))
    internal abstract fun contributeShowRecipeFromLinkActivity(): ShowRecipeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(RecipesListModule::class))
    internal abstract fun contributeRecipesListActivity(): RecipesListActivity

}