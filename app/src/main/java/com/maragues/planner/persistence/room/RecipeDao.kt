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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(recipe: Recipe)
}