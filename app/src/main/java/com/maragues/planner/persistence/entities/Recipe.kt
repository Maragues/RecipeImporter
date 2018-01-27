package com.maragues.planner.persistence.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by miguelaragues on 7/1/18.
 */
@Entity
data class Recipe(@ColumnInfo(name = "title") val title: String,
                  @ColumnInfo(name = "screenshot") val screenshot: String,
                  @ColumnInfo(name = "description") val description: String,
                  @ColumnInfo(name = "url") val url: String,
                  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 1)