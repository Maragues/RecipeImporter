package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by miguelaragues on 7/1/18.
 */
interface RecipeRepository {
    fun save(recipe: Recipe): Single<Long>

    fun list(): Flowable<List<Recipe>>

    fun filterByTag(tagFilter: Observable<Set<Tag>>): Flowable<List<Recipe>>

    fun filterByName(query: String): Flowable<List<Recipe>>

    fun read(recipeId: Long): Single<Recipe>
}