package com.maragues.planner.persistence.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.maragues.planner.persistence.entities.Recipe;

/**
 * Created by miguelaragues on 20/11/17.
 */
@Database(
    entities = {
        Recipe.class,
    },
    version = 1)
public abstract class RoomAppDatabase extends RoomDatabase {

  abstract RecipeDao recipeDao();

}
