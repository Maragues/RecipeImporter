package com.maragues.planner.recipes

import com.maragues.planner.persistence.entities.Recipe

/**
 * Created by miguelaragues on 7/1/18.
 */
internal data class RecipesListViewState(
        val recipes: List<Recipe>,
        val hoveringPlannerViewState: HoveringPlannerViewState
) {
    fun withRecipes(recipes: List<Recipe>): RecipesListViewState {
        return RecipesListViewState(recipes, hoveringPlannerViewState)
    }
}