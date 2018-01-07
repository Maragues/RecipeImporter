package com.maragues.planner.di

import com.maragues.planner.recipeFromLink.NewRecipeFromLinkActivity
import com.maragues.planner.recipeFromLink.RecipeFromLinkModule
import com.maragues.planner.recipes.RecipesListActivity
import com.maragues.planner.recipes.RecipesListModule

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di
 */
@Module
abstract class BindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(RecipeFromLinkModule::class))
    internal abstract fun contributeNewRecipeFromLinkActivity(): NewRecipeFromLinkActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(RecipesListModule::class))
    internal abstract fun contributeRecipesListActivity(): RecipesListActivity

}