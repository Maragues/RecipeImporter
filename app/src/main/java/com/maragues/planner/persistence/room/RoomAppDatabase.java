package com.maragues.planner.persistence.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import com.maragues.planner.persistence.entities.MealSlotRecipe;
import com.maragues.planner.persistence.entities.Recipe;
import com.maragues.planner.persistence.entities.Tag;

/**
 * Created by miguelaragues on 20/11/17.
 */
@Database(
    entities = {
        Recipe.class,
        MealSlotRecipe.class,
        Tag.class
    },
    version = 1)
@TypeConverters(RoomTypeConverters.class)
public abstract class RoomAppDatabase extends RoomDatabase {

  abstract RecipeDao recipeDao();

  abstract MealSlotDao mealSlotDao();

  abstract TagDao tagDao();

}
