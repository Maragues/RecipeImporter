package com.maragues.planner.recipes

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag

internal sealed class RecipeListPartialState {
    abstract fun computeViewState(previousState: RecipesListViewState): RecipesListViewState
}

internal class ShowFilterTagsDialogAction : RecipeListPartialState() {
    override fun computeViewState(previousState: RecipesListViewState): RecipesListViewState {
        return previousState.withAction(ACTION_SHOW_FILTER_TAG_DIALOG)
    }
}

internal class NoAction : RecipeListPartialState() {
    override fun computeViewState(previousState: RecipesListViewState): RecipesListViewState {
        return previousState.withAction(ACTION_NONE)
    }
}

internal class TagFiltersPartialState(val tags: Set<Tag>) : RecipeListPartialState() {
    override fun computeViewState(previousState: RecipesListViewState): RecipesListViewState {
        return previousState.withTagsFilter(tags)
    }
}

internal class RecipesPartialState(val recipes: List<Recipe>) : RecipeListPartialState() {
    override fun computeViewState(previousState: RecipesListViewState): RecipesListViewState {
        return previousState.withRecipes(recipes)
    }
}