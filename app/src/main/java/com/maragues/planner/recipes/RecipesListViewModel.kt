package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.RecipeRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.Collections

/**
 * Created by miguelaragues on 7/1/18.
 */
class RecipesListViewModel(val recipesRepository: RecipeRepository) : BaseViewModel() {

    class Factory(val recipesRepository: RecipeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipesListViewModel(recipesRepository) as T
        }
    }

    private val recipeListViewStateSubject: BehaviorSubject<RecipesListViewState> = BehaviorSubject.create()

    fun viewStateObservable(): Observable<RecipesListViewState> {
        return recipeListViewStateSubject
                .startWith(RecipesListViewState(Collections.emptyList()))
                .doOnSubscribe({ loadRecipes() })
                .hide()
    }

    private fun loadRecipes() {
        disposables().add(recipesRepository.list()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { onRecipesLoaded(it) },
                        { it.printStackTrace() }
                ))
    }

    private fun onRecipesLoaded(recipes: List<Recipe>) {
        recipeListViewStateSubject.onNext(RecipesListViewState(recipes))
    }
}