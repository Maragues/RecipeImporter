package com.maragues.planner.recipes

import android.support.annotation.IntDef
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag

/**
 * Created by miguelaragues on 7/1/18.
 */
internal data class RecipesListViewState(
        val recipes: List<Recipe>,
        val tagFilters: Set<Tag>,
        @ActionId val actionId: Int
) {
    fun withRecipes(recipes: List<Recipe>): RecipesListViewState {
        return RecipesListViewState(recipes, tagFilters, ACTION_NONE)
    }

    fun withAction(@ActionId actionId: Int): RecipesListViewState {
        return RecipesListViewState(recipes, tagFilters, actionId)
    }

    fun withTagsFilter(tagFilters: Set<Tag>): RecipesListViewState {
        return RecipesListViewState(recipes, tagFilters, ACTION_NONE)
    }
}


@Retention(AnnotationRetention.SOURCE)
@IntDef(ACTION_NONE, ACTION_SHOW_FILTER_TAG_DIALOG)
internal annotation class ActionId

const val ACTION_NONE = 0
const val ACTION_SHOW_FILTER_TAG_DIALOG = 1