package com.maragues.planner.recipes

import com.maragues.planner.di.ActivityScope
import com.maragues.planner.persistence.repositories.RecipeRepository
import dagger.Module
import dagger.Provides

@Module
@ActivityScope
class RecipesListModule {

    @Module
    @ActivityScope
    companion object {

        @JvmStatic
        @Provides
        fun providesViewModelFactory(recipesRepository: RecipeRepository): RecipesListViewModel.Factory {
            return RecipesListViewModel.Factory(recipesRepository)
        }
    }
}