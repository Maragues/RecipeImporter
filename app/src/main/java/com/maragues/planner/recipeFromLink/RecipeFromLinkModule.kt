package com.maragues.planner.recipeFromLink

import com.maragues.planner.di.ActivityScope
import com.maragues.planner.interactors.RecipeInteractor
import dagger.Module
import dagger.Provides
import javax.inject.Named

@ActivityScope
@Module
class RecipeFromLinkModule {

    @ActivityScope
    @Module
    companion object {
        const val RECIPE_URL = "recipe_url"

        @JvmStatic
        @Provides
        @Named(RECIPE_URL)
        fun providesUrl(activity: NewRecipeFromLinkActivity): String {
            return activity.getUrlToParse()
        }

        @JvmStatic
        @Provides
        fun providesViewModelFactory(@Named(RECIPE_URL) url: String,
                                     scrapper: RecipeLinkScrapper,
                                     recipeInteractor: RecipeInteractor,
                                     recipeFromLinkNavigator: RecipeFromLinkNavigator): RecipeFromLinkViewModel.Factory {
            return RecipeFromLinkViewModel.Factory(url, scrapper, recipeInteractor, recipeFromLinkNavigator)
        }
    }
}