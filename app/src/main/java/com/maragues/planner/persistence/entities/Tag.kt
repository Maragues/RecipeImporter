package com.maragues.planner.persistence.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by miguelaragues on 25/2/18.
 */
@Entity
data class Tag(@PrimaryKey @ColumnInfo(name = "name") val name: String)
