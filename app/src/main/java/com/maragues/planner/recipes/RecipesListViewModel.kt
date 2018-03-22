package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jakewharton.rx.ReplayingShare
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.AddTag
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.RemoveTag
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.entities.TagAction
import com.maragues.planner.persistence.repositories.RecipeRepository
import com.maragues.planner.recipeFromLink.addTag.AddTagDialogFragment.TagSelectedListener
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import java.util.Collections
import javax.inject.Inject

/**
 * Created by miguelaragues on 7/1/18.
 */
internal class RecipesListViewModel(val recipesRepository: RecipeRepository) : BaseViewModel(), TagSelectedListener {


    private val recipeListViewStateSubject: BehaviorSubject<RecipesListViewState> = BehaviorSubject.create()

    private var viewState: RecipesListViewState = initialViewState()

    private val actionIdSubject = BehaviorSubject.create<Int>()
    private val recipesSubject = BehaviorSubject.create<List<Recipe>>()
    private val tagSubject = ReplaySubject.create<TagAction>()

    private val viewStateObservable: Observable<RecipesListViewState> by lazy { initViewStateObservable() }

    fun viewStateObservable(): Observable<RecipesListViewState> {
        return viewStateObservable
    }

    private fun tagsObservable(): Observable<TagFiltersPartialState> {
        return tagSubject
                .scan(setOf(), BiFunction<Set<Tag>, TagAction, Set<Tag>> { oldTags, tagAction ->
                    tagAction.apply(oldTags)
                })
                .map { TagFiltersPartialState(it) }
    }

    private fun recipesObservable(): Observable<RecipesPartialState>? {
        return recipesSubject
                .doOnSubscribe({ loadRecipes() })
                .startWith(listOf<Recipe>())
                .map { RecipesPartialState(it) }
    }

    private fun actionIdObservable(): Observable<RecipeListPartialState> {
        return actionIdSubject
                .map {
                    when (it) {
                        ACTION_SHOW_FILTER_TAG_DIALOG -> ShowFilterTagsDialogAction()
                        else -> {
                            NoAction()
                        }
                    }
                }
                .doAfterNext { if (it !is NoAction) actionIdSubject.onNext(ACTION_NONE) }
                .distinctUntilChanged()
    }

    private fun initViewStateObservable(): Observable<RecipesListViewState> {
        val partialStateObservable = Observable.merge(
                tagsObservable(),
                recipesObservable(),
                actionIdObservable()
        )

        return partialStateObservable
                .scan(initialViewState(), { state, partialState -> partialState.computeViewState(state) })
                .compose(ReplayingShare.instance())
    }

    private fun initialViewState() = RecipesListViewState(
            Collections.emptyList(),
            setOf(),
            ACTION_NONE
    )

    private fun loadRecipes() {
        disposables().add(recipesRepository.filterByTag(tagsObservable().map { it.tags })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        recipesSubject::onNext,
                        Throwable::printStackTrace
                ))
    }

    override fun onTagSelected(tag: Tag) {
        tagSubject.onNext(AddTag(tag))
    }

    fun onTagRemoved(tag: Tag) {
        tagSubject.onNext(RemoveTag(tag))
    }

    fun onTagFilterClicked() {
        actionIdSubject.onNext(ACTION_SHOW_FILTER_TAG_DIALOG)
    }

    class Factory
    @Inject constructor(val recipesRepository: RecipeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipesListViewModel(recipesRepository) as T
        }
    }
}