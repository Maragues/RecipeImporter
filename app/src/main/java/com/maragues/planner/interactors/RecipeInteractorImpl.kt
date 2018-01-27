package com.maragues.planner.interactors

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.RecipeRepository
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by miguelaragues on 27/1/18.
 */
class RecipeInteractorImpl
@Inject constructor(val recipeRepository: RecipeRepository) : RecipeInteractor {
    override fun storeOrUpdate(recipe: Recipe): Completable {
        return recipeRepository.save(recipe)
    }
}