package com.maragues.planner.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.Query
import com.maragues.planner.persistence.entities.Tag
import io.reactivex.Flowable

/**
 * Created by miguelaragues on 7/1/18.
 */
@Dao
internal abstract class TagDao {

    @Insert
    abstract fun insert(tag: Tag)

    @Insert(onConflict = IGNORE)
    abstract fun insert(tag: List<Tag>)

    @Query("SELECT * FROM tag")
    abstract fun list(): Flowable<List<Tag>>

    @Query("SELECT * FROM tag where INSTR(LOWER(name), LOWER(:filter)) > 0 OR LENGTH(:filter) = 0")
    abstract fun listFilteredBy(filter: String): Flowable<List<Tag>>

    @Query("SELECT * from tag INNER JOIN recipeTag ON tagName=name AND recipeId=:recipeId")
    abstract fun recipeTags(recipeId: Long): Flowable<List<Tag>>

}
