package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.interactors.RecipeInteractor
import com.maragues.planner.persistence.entities.Recipe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RecipeFromLinkViewModel(val urlToScrap: String,
                              val recipeLinkScrapper: RecipeLinkScrapper,
                              val recipeInteractor: RecipeInteractor,
                              val navigator: RecipeFromLinkNavigator) : BaseViewModel() {

    private val viewStateBehaviorSubject: BehaviorSubject<RecipeFromLinkViewState> = BehaviorSubject.create()

    internal fun viewStateObservable(): Observable<RecipeFromLinkViewState> {
        return viewStateBehaviorSubject
                .doOnSubscribe({ scrapRecipe() })
                .hide()
    }

    @VisibleForTesting
    fun scrapRecipe() {
        disposables().add(recipeLinkScrapper.scrape(urlToScrap)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .subscribe(
                        { scrappedRecipe -> onRecipeScrapped(scrappedRecipe) },
                        { error -> error.printStackTrace() }
                ));
    }

    @VisibleForTesting
    internal fun onRecipeScrapped(scrappedRecipe: ScrappedRecipe) {
        viewStateBehaviorSubject.onNext(RecipeFromLinkViewState(scrappedRecipe))
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
        val scrappedRecipe = viewStateBehaviorSubject.value.scrappedRecipe

        return Recipe(scrappedRecipe.title, scrappedRecipe.image, scrappedRecipe.description, scrappedRecipe.link)
    }

    class Factory(private val urlToScrap: String,
                  private val recipeLinkScrapper: RecipeLinkScrapper,
                  private val recipeInteractor: RecipeInteractor,
                  private val navigator: RecipeFromLinkNavigator) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeFromLinkViewModel(urlToScrap,  recipeLinkScrapper, recipeInteractor, navigator) as T
        }
    }

}
