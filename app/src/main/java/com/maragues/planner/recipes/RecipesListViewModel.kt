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
import javax.inject.Inject

/**
 * Created by miguelaragues on 7/1/18.
 */
class RecipesListViewModel(val recipesRepository: RecipeRepository) : BaseViewModel() {

    private val recipeListViewStateSubject: BehaviorSubject<RecipesListViewState> = BehaviorSubject.create()

    private var viewState: RecipesListViewState = initialViewState()

    internal fun viewStateObservable(): Observable<RecipesListViewState> {
        return recipeListViewStateSubject
                .startWith(viewState)
                .doOnSubscribe({ loadRecipes() })
                .doOnNext({ viewState = it })
    }

    private fun initialViewState() = RecipesListViewState(
            Collections.emptyList()
    )

    private fun loadRecipes() {
        disposables().add(recipesRepository.list()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { onRecipesLoaded(it) },
                        { it.printStackTrace() }
                ))
    }

    private fun onRecipesLoaded(recipes: List<Recipe>) {
        recipeListViewStateSubject.onNext(viewState.withRecipes(recipes))
    }

    class Factory
    @Inject constructor(val recipesRepository: RecipeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipesListViewModel(recipesRepository) as T
        }
    }
}