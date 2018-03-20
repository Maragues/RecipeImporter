package com.maragues.planner.interactors

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.repositories.RecipeRepository
import com.maragues.planner.persistence.repositories.RecipeTagRepository
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by miguelaragues on 27/1/18.
 */
class RecipeInteractorImpl
@Inject constructor(val recipeRepository: RecipeRepository, val tagInteractor: TagInteractor) : RecipeInteractor {
    override fun storeOrUpdate(recipe: Recipe, tags: Set<Tag>): Completable {
        return recipeRepository.save(recipe)
                .flatMapCompletable { tagInteractor.storeOrUpdate(it, tags) }
    }
}