package com.maragues.planner.persistence.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by miguelaragues on 25/2/18.
 */
@Entity
data class Tag(@PrimaryKey @ColumnInfo(name = "name") val name: String)

internal sealed class TagAction {
    abstract fun apply(oldTags: Set<Tag>): Set<Tag>
}

internal data class AddTag(val tag: Tag) : TagAction() {
    override fun apply(oldTags: Set<Tag>): Set<Tag> {
        val tagSet = mutableSetOf(tag)

        tagSet.addAll(oldTags)

        return tagSet
    }
}

internal data class RemoveTag(val tag: Tag) : TagAction() {
    override fun apply(oldTags: Set<Tag>) = oldTags.minus(tag)
}