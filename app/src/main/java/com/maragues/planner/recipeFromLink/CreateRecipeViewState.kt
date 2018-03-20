package com.maragues.planner.recipeFromLink

import android.support.annotation.IntDef
import com.maragues.planner.persistence.entities.Tag

internal data class CreateRecipeViewState(
        val scrapedRecipe: ScrapedRecipe,
        val tags: Set<Tag>,
        val scrapInProgress: Boolean,
        @ActionId val actionId: Int,
        val errorLoadingScrappedRecipe: String
) {
    companion object {
        val EMPTY = CreateRecipeViewState(ScrapedRecipe.EMPTY, setOf(), false, ACTION_NONE, "")
    }

    fun withScrapInProgress(url: String): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe.withLink(url), tags, true, actionId, errorLoadingScrappedRecipe)
    }

    fun withScrappedRecipe(scrappedRecipe: ScrapedRecipe): CreateRecipeViewState {
        return CreateRecipeViewState(scrappedRecipe, tags, false, actionId, "")
    }

    fun withErrorScrappingRecipe(error: String): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe, tags, false, actionId, error)
    }

    fun withAction(@ActionId actionId: Int): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe, tags, scrapInProgress, actionId, "")
    }

    fun withTags(newTags: Set<Tag>): CreateRecipeViewState {
        return CreateRecipeViewState(scrapedRecipe, newTags, scrapInProgress, actionId, errorLoadingScrappedRecipe)
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(ACTION_NONE, ACTION_SHOW_URL_DIALOG, ACTION_SHOW_ADD_TAG_DIALOG)
internal annotation class ActionId

const val ACTION_NONE = 0
const val ACTION_SHOW_URL_DIALOG = 1
const val ACTION_SHOW_ADD_TAG_DIALOG = 2