package com.maragues.planner.interactors

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import io.reactivex.Completable

/**
 * Created by miguelaragues on 27/1/18.
 */
interface RecipeInteractor {
    fun storeOrUpdate(recipe: Recipe, tags: Set<Tag>) : Completable
}