package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.RecipeTag
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.room.RecipeTagDao
import javax.inject.Inject

/**
 * Created by miguelaragues on 10/3/18.
 */
internal class RecipeTagRepositoryRoomImpl
@Inject constructor(val recipeTagDao: RecipeTagDao) : RecipeTagRepository {
    override fun replaceTags(recipeId: Long, tags: Set<Tag>) {
        recipeTagDao.replaceTags(recipeId, tags)
    }

    override fun insert(recipeTag: RecipeTag) {
        recipeTagDao.insert(recipeTag)
    }
}