package com.maragues.planner.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.maragues.planner.persistence.entities.RecipeTag
import com.maragues.planner.persistence.entities.Tag

/**
 * Created by miguelaragues on 10/3/18.
 */
@Dao
internal abstract class RecipeTagDao {

    @Insert
    abstract fun insert(recipeTags: RecipeTag)

    @Query("DELETE from recipeTag WHERE recipeId=:recipeId")
    abstract fun deleteAllTagsForRecipe(recipeId: Long)

    @Transaction
    open fun replaceTags(recipeId: Long, tags: Set<Tag>) {
        deleteAllTagsForRecipe(recipeId)

        tags.forEach { insert(RecipeTag(it.name, recipeId)) }
    }
}