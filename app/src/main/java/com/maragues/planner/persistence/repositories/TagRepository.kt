package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Tag
import io.reactivex.Flowable

/**
 * Created by miguelaragues on 3/3/18.
 */
interface TagRepository {
    fun list(): Flowable<List<Tag>>
    fun listFilteredBy(filter: String): Flowable<List<Tag>>
    fun insert(tag: Tag)
    fun insert(tags: List<Tag>)
}