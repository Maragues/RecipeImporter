package com.maragues.planner.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.maragues.planner.persistence.entities.Recipe
import io.reactivex.Flowable

/**
 * Created by miguelaragues on 7/1/18.
 */
@Dao
abstract class RecipeDao {

    @Query("SELECT * FROM recipe")
    abstract fun readAll(): Flowable<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id IN (SELECT recipeId FROM recipeTag WHERE tagName IN (:tagNamesCommaSeparated)) OR LENGTH(:tagNamesCommaSeparated) = 0")
    abstract fun filterByTag(tagNamesCommaSeparated: String): Flowable<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE INSTR(LOWER(title), LOWER(:filter)) > 0 OR LENGTH(:filter) = 0")
    abstract fun filterByName(filter: String): Flowable<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id IN (:ids)")
    abstract fun recipesByIds(ids: String): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(recipe: Recipe): Long
}