package com.maragues.planner.persistence.room;

import android.arch.persistence.room.Room;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by miguelaragues on 20/11/17.
 */
@Module
public class RoomModule {

  @Provides
  @Singleton
  static RoomAppDatabase providesAppDatabase(Context context) {
    return Room.databaseBuilder(context, RoomAppDatabase.class, "planner")
//    return Room.inMemoryDatabaseBuilder(context, RoomAppDatabase.class)
        .build();
  }

  @Provides
  static RecipeDao providesRecipeDao(RoomAppDatabase appDatabase) {
    return appDatabase.recipeDao();
  }

  @Provides
  static MealSlotDao providesMealSlotDao(RoomAppDatabase appDatabase) {
    return appDatabase.mealSlotDao();
  }
}
