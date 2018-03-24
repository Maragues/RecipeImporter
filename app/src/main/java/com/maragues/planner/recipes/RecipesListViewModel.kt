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
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import java.util.Collections
import javax.inject.Inject

/**
 * Created by miguelaragues on 7/1/18.
 */
internal class RecipesListViewModel(val recipesRepository: RecipeRepository) : BaseViewModel(), TagSelectedListener {

    private val actionIdSubject = BehaviorSubject.create<Int>()
    private val recipesSubject = BehaviorSubject.create<List<Recipe>>()
    private val tagSubject = ReplaySubject.create<TagAction>()
    val searchSubject = PublishSubject.create<String>()

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

    private fun recipesObservable(): Observable<RecipesPartialState> {
        return Flowable.merge(filterByTagObservable(), searchRecipeObservable())
                .toObservable()
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

    private fun filterByTagObservable() = recipesRepository.filterByTag(tagsObservable().map { it.tags })

    private fun searchRecipeObservable(): Flowable<List<Recipe>> {
        return searchSubject
                .toFlowable(BUFFER)
                .switchMap { recipesRepository.filterByName(it) }
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

    fun onSearchRecipe(queryText: String) {
        searchSubject.onNext(queryText)
    }

    class Factory
    @Inject constructor(val recipesRepository: RecipeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipesListViewModel(recipesRepository) as T
        }
    }
}