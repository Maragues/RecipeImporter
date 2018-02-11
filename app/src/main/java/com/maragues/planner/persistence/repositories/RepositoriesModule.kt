package com.maragues.planner.persistence.repositories

import dagger.Binds
import dagger.Module

/**
 * Created by miguelaragues on 7/1/18.
 */
@Module
abstract class RepositoriesModule {

    @Binds
    internal abstract fun bindsRecipeRepository(recipesRepositoryRoom: RecipesRepositoryRoomImpl): RecipeRepository

    @Binds
    internal abstract fun bindsMealSlotRepository(recipesRepositoryRoom: MealSlotRepositoryRoomImpl): MealSlotRepository
}