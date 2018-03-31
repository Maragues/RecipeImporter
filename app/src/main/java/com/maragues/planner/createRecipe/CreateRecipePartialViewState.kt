package com.maragues.planner.createRecipe

import com.maragues.planner.persistence.entities.Tag

/**
 * Created by miguelaragues on 24/2/18.
 */
internal sealed class CreateRecipePartialViewState {
    abstract fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState
}

internal data class ScrapInProgress(val url: String) : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withScrapInProgress(url)
    }
}

internal data class RecipeScrapped(val scrappedRecipe: ScrapedRecipe) : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withScrappedRecipe(scrappedRecipe)
    }
}

internal data class ErrorScrappingRecipe(val errorMessage: String) : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withErrorScrappingRecipe(errorMessage)
    }
}

internal class NoUrlToScrap : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withScrappedRecipe(ScrapedRecipe.EMPTY)
    }
}

internal class NoAction : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withAction(ACTION_NONE)
    }
}

internal class ShowUrlAction : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withAction(ACTION_SHOW_URL_DIALOG)
    }
}

internal data class UserTypedUrl(val url: String) : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withScrappedRecipe(previousState.scrapedRecipe.withLink(url))
    }
}

internal class ShowAddTagDialogAction : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withAction(ACTION_SHOW_ADD_TAG_DIALOG)
    }
}

internal class RecipeTagsPartialState(val tags: Set<Tag>) : CreateRecipePartialViewState() {
    override fun computeViewState(previousState: CreateRecipeViewState): CreateRecipeViewState {
        return previousState.withTags(tags)
    }
}