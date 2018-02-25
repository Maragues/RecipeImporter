package com.maragues.planner.recipeFromLink

import android.support.annotation.IntDef

internal data class CreateRecipeViewState(
        val scrapedRecipe: ScrapedRecipe,
        val scrapInProgress: Boolean,
        @ActionId val actionId: Long,
        val errorLoadingScrappedRecipe: String
) {
    companion object {
        val EMPTY = CreateRecipeViewState(ScrapedRecipe.EMPTY, false, ACTION_NONE, "")
    }

    fun withScrapInProgress(url: String): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe.withLink(url), true, actionId, errorLoadingScrappedRecipe)
    }

    fun withScrappedRecipe(scrappedRecipe: ScrapedRecipe): CreateRecipeViewState {
        return CreateRecipeViewState(scrappedRecipe, false, actionId, "")
    }

    fun withErrorScrappingRecipe(error: String): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe, false, actionId, error)
    }

    fun withAction(@ActionId actionId: Long): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe, scrapInProgress, actionId, "")
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(ACTION_NONE, ACTION_SHOW_URL_DIALOG)
internal annotation class ActionId

const val ACTION_NONE = 0L
const val ACTION_SHOW_URL_DIALOG = 1L