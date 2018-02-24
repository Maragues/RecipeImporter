package com.maragues.planner.recipeFromLink

import dagger.Module
import dagger.Provides
import javax.annotation.Nullable

@Module
class RecipeFromLinkModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Nullable
        fun providesUrl(activity: NewRecipeActivity): String? {
            return activity.getUrlToParse()
        }
    }
}