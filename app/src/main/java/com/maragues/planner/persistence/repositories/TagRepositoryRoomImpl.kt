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
    override fun listFilteredBy(filter: String): Flowable<List<Tag>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun list(): Flowable<List<Tag>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}