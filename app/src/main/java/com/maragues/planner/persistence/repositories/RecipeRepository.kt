package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Recipe
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by miguelaragues on 7/1/18.
 */
interface RecipeRepository {
    fun save(recipe: Recipe) : Single<Long>

    fun list(): Flowable<List<Recipe>>
}