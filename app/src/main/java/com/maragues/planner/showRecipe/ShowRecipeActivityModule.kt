package com.maragues.planner.showRecipe

import dagger.Module
import dagger.Provides

@Module
abstract class ShowRecipeActivityModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        internal fun providesRecipeId(activity: ShowRecipeActivity) = activity.recipeId()
    }
}
