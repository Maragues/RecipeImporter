package com.maragues.planner.persistence.room

import android.arch.persistence.room.TypeConverter
import com.maragues.planner.recipes.model.MealType
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 11/2/18.
 */
class RoomTypeConverters {

    @TypeConverter
    fun stringToLocalDate(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun localDateToString(localDate: LocalDate): String {
        return localDate.toString()
    }

    @TypeConverter
    fun mealTypeFromString(value: String): MealType {
        return MealType.valueOf(value)
    }

    @TypeConverter
    fun mealTypeToString(localDate: MealType): String {
        return localDate.toString()
    }

}