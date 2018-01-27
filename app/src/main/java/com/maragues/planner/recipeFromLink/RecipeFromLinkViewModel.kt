package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.IntDef
import android.support.annotation.VisibleForTesting
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.RecipeRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class RecipeFromLinkViewModel(val urlToScrap: String,
                              val recipeTitle: String,
                              val recipeLinkScrapper: RecipeLinkScrapper,
                              val recipesRepository: RecipeRepository,
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

        disposables().add(recipesRepository.save(recipe)
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

        return Recipe(scrappedRecipe.title, scrappedRecipe.image, scrappedRecipe.link)
    }

    class Factory(val urlToScrap: String,
                  val title: String,
                  val recipeLinkScrapper: RecipeLinkScrapper,
                  val recipesRepository: RecipeRepository,
                  val navigator: RecipeFromLinkNavigator) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeFromLinkViewModel(urlToScrap, title, recipeLinkScrapper, recipesRepository, navigator) as T
        }
    }

}
