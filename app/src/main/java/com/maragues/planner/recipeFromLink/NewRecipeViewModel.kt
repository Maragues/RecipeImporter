package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jakewharton.rx.ReplayingShare
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.interactors.RecipeInteractor
import com.maragues.planner.persistence.entities.Recipe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

internal class NewRecipeViewModel(
        private val urlToScrap: String?,
        private val recipeLinkScrapper: RecipeLinkScrapper,
        private val recipeInteractor: RecipeInteractor,
        private val navigator: RecipeFromLinkNavigator) : BaseViewModel() {

    private val viewStateBehaviorSubject = BehaviorSubject.create<CreateRecipeViewState>()

    private val actionIdSubject = BehaviorSubject.create<Long>()

    private var viewStateObservable: Observable<CreateRecipeViewState>? = null

    private fun scrappedRecipeObservable(): Observable<CreateRecipePartialViewState> {
        if (urlToScrap == null)
            return Observable.just(NoUrlToScrap())

        return recipeLinkScrapper.scrape(urlToScrap)
                .subscribeOn(Schedulers.io())
                .map { RecipeScrapped(it) as CreateRecipePartialViewState }
                .onErrorReturn({ ErrorScrappingRecipe("Error ${it.message}") })
                .toObservable()
                .startWith(ScrapInProgress(urlToScrap))
    }

    internal fun onLinkClicked() {
        actionIdSubject.onNext(ACTION_SHOW_URL_DIALOG)
    }

    private fun actionIdObservable(): Observable<CreateRecipePartialViewState> {
        return actionIdSubject
                .map {
                    when (it) {
                        ACTION_SHOW_URL_DIALOG -> ShowUrlAction()
                        else -> {
                            NoAction()
                        }
                    }
                }
    }

    internal fun viewStateObservable(): Observable<CreateRecipeViewState> {
        if (viewStateObservable == null) {
            val partialStateObservable = Observable.merge(scrappedRecipeObservable(), actionIdObservable())

            val initialState = CreateRecipeViewState.EMPTY

            viewStateObservable = partialStateObservable
                    .scan(initialState, { state, partialState -> partialState.computeViewState(state) })
                    .compose(ReplayingShare.instance())
        }

        return viewStateObservable!!
    }

    fun onSaveClicked() {
        val recipe = createRecipe()

        disposables().add(recipeInteractor.storeOrUpdate(recipe)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { navigator.navigateToRecipeList() },
                        Throwable::printStackTrace
                ))
    }

    private fun createRecipe(): Recipe {
        val scrappedRecipe = viewStateBehaviorSubject.value.scrapedRecipe

        return Recipe(scrappedRecipe.title, scrappedRecipe.image, scrappedRecipe.description, scrappedRecipe.link)
    }

    fun userClickedUrlIcon() {

    }

    class Factory
    @Inject constructor(
            private val urlToScrap: String?,
            private val recipeLinkScrapper: RecipeLinkScrapper,
            private val recipeInteractor: RecipeInteractor,
            private val navigator: RecipeFromLinkNavigator) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewRecipeViewModel(urlToScrap, recipeLinkScrapper, recipeInteractor, navigator) as T
        }
    }
}
