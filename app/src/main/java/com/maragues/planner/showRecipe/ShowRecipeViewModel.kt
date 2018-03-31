package com.maragues.planner.showRecipe

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.repositories.RecipeRepository
import com.maragues.planner.persistence.repositories.TagRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

internal class ShowRecipeViewModel(
        private val recipeId: Long,
        private val recipeRepository: RecipeRepository,
        private val tagRepository: TagRepository
) : BaseViewModel() {
    fun viewStateObservable(): Observable<ShowRecipeViewState> {
        return Observable.zip(
                loadRecipeObservable(),
                loadTagsObservable(),
                BiFunction<Recipe, List<Tag>, ShowRecipeViewState> { recipe: Recipe, tags: List<Tag> ->
                    ShowRecipeViewState(recipe, tags)
                }
        )
    }

    private fun loadRecipeObservable() = recipeRepository.read(recipeId).toObservable()

    private fun loadTagsObservable() = tagRepository.tagsByRecipeId(recipeId).toObservable()

    class Factory
    @Inject constructor(
            private val recipeRepository: RecipeRepository,
            private val tagRepository: TagRepository,
            private val recipeId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShowRecipeViewModel(recipeId, recipeRepository, tagRepository) as T
        }
    }
}


data class ShowRecipeViewState(val recipe: Recipe, val tags: List<Tag>) {

}