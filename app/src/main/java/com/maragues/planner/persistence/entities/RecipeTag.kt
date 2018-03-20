package com.maragues.planner.persistence.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey

/**
 * Created by miguelaragues on 11/2/18.
 */

@Entity(
        primaryKeys = ["tagName", "recipeId"],
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Recipe::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("recipeId")
                ),
                ForeignKey(
                        entity = Tag::class,
                        parentColumns = arrayOf("name"),
                        childColumns = arrayOf("tagName")
                )
        )
)
data class RecipeTag(
        val tagName: String,
        val recipeId: Long
)