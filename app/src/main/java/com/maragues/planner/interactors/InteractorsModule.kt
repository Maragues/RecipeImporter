package com.maragues.planner.interactors

import dagger.Binds
import dagger.Module

/**
 * Created by miguelaragues on 7/1/18.
 */
@Module
abstract class InteractorsModule {

    @Binds
    internal abstract fun bindsRecipeInteractor(recipesInteractor: RecipeInteractorImpl): RecipeInteractor
}