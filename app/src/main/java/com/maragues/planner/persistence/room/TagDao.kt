package com.maragues.planner.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import io.reactivex.Flowable

/**
 * Created by miguelaragues on 7/1/18.
 */
@Dao
abstract class TagDao {

    @Query("SELECT * FROM tag")
    abstract fun list(): Flowable<List<Tag>>

    @Query("SELECT * FROM tag where instr(name, :filter) > 0")
    abstract fun listFilteredBy(filter: String): Flowable<List<Tag>>
}