package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.room.TagDao
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by miguelaragues on 3/3/18.
 */
internal class TagRepositoryRoomImpl
@Inject constructor(val tagDao: TagDao) : TagRepository {
    override fun tagsByRecipeId(recipeId: Long): Flowable<List<Tag>> {
        return tagDao.listForRecipeId(recipeId)
    }

    override fun insert(tags: List<Tag>) {
        tagDao.insert(tags)
    }

    override fun insert(tag: Tag) {
        tagDao.insert(tag)
    }

    override fun listFilteredBy(filter: String): Flowable<List<Tag>> {
        return tagDao.listFilteredBy(filter)
    }

    override fun list(): Flowable<List<Tag>> {
        return tagDao.list()
    }
}