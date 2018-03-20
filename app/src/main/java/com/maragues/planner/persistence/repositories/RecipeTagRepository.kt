package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.RecipeTag
import com.maragues.planner.persistence.entities.Tag

/**
 * Created by miguelaragues on 10/3/18.
 */
interface RecipeTagRepository {
    fun insert(recipeTag: RecipeTag)
    fun replaceTags(recipeId: Long, tags: Set<Tag>)
}