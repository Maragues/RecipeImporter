package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Recipe
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by miguelaragues on 7/1/18.
 */
interface RecipeRepository {
    fun save(recipe: Recipe) : Completable

    fun list(): Flowable<List<Recipe>>
}