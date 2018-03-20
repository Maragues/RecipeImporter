package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jakewharton.rx.ReplayingShare
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.interactors.RecipeInteractor
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.repositories.RecipeTagRepository
import com.maragues.planner.recipeFromLink.NewRecipeActivity.UserRecipeFields
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import javax.inject.Inject

internal class NewRecipeViewModel(
        private val urlToScrap: String?,
        private val recipeLinkScrapper: RecipeLinkScrapper,
        private val recipeInteractor: RecipeInteractor,
        private val recipeTagRepository: RecipeTagRepository,
        private val navigator: RecipeFromLinkNavigator) : BaseViewModel() {

    private val actionIdSubject = BehaviorSubject.create<Int>()
    private val userTypedUrlSubject = PublishSubject.create<String>()
    private val tagSubject = ReplaySubject.create<TagAction>()

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

    private fun userTypedUrlObservable(): Observable<CreateRecipePartialViewState> {
        return userTypedUrlSubject.map { UserTypedUrl(it) }
    }

    private fun actionIdObservable(): Observable<CreateRecipePartialViewState> {
        return actionIdSubject
                .map {
                    when (it) {
                        ACTION_SHOW_URL_DIALOG -> ShowUrlAction()
                        ACTION_SHOW_ADD_TAG_DIALOG -> ShowAddTagDialogAction()
                        else -> {
                            NoAction()
                        }
                    }
                }
                .doAfterNext { if (it !is NoAction) actionIdSubject.onNext(ACTION_NONE) }
                .distinctUntilChanged()
    }

    private fun tagsObservable(): Observable<RecipeTagsPartialState> {
        return tagSubject
                .scan(setOf(), BiFunction<Set<Tag>, TagAction, Set<Tag>> { oldTags, tagAction ->
                    tagAction.apply(oldTags)
                })
                .map { RecipeTagsPartialState(it) }
    }

    internal fun viewStateObservable(): Observable<CreateRecipeViewState> {
        if (viewStateObservable == null) {
            val partialStateObservable = Observable.merge(
                    scrappedRecipeObservable(),
                    userTypedUrlObservable(),
                    actionIdObservable(),
                    tagsObservable()
            )

            val initialState = CreateRecipeViewState.EMPTY

            viewStateObservable = partialStateObservable
                    .scan(initialState, { state, partialState -> partialState.computeViewState(state) })
                    .compose(ReplayingShare.instance())
        }

        return viewStateObservable!!
    }

    fun onSaveClicked(userRecipeFields: UserRecipeFields) {
        val recipe = createRecipe(userRecipeFields)

        val tags = tags()

        disposables().add(recipeInteractor.storeOrUpdate(recipe, tags)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { navigator.navigateToRecipeList() },
                        Throwable::printStackTrace
                ))
    }

    private fun tags(): Set<Tag> {
        return viewState().tags
    }

    private fun createRecipe(userRecipeFields: UserRecipeFields): Recipe {
        val scrappedRecipe = viewState().scrapedRecipe

        return Recipe(userRecipeFields.title, scrappedRecipe.image, userRecipeFields.description, scrappedRecipe.link)
    }

    private fun viewState(): CreateRecipeViewState {
        if (viewStateObservable == null) return CreateRecipeViewState.EMPTY

        return viewStateObservable!!.take(1).blockingFirst()
    }

    fun userClickedUrlIcon() {
        actionIdSubject.onNext(ACTION_SHOW_URL_DIALOG)
    }

    fun userEnteredNewRecipeLink(newLink: CharSequence) {
        if (!newLink.isEmpty()) userTypedUrlSubject.onNext(newLink.toString())
    }

    class Factory
    @Inject constructor(
            private val urlToScrap: String?,
            private val recipeLinkScrapper: RecipeLinkScrapper,
            private val recipeTagRepository: RecipeTagRepository,
            private val recipeInteractor: RecipeInteractor,
            private val navigator: RecipeFromLinkNavigator) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewRecipeViewModel(urlToScrap, recipeLinkScrapper, recipeInteractor, recipeTagRepository, navigator) as T
        }
    }

    fun onAddTagClicked() {
        actionIdSubject.onNext(ACTION_SHOW_ADD_TAG_DIALOG)
    }

    fun onTagSelected(tag: Tag) {
        tagSubject.onNext(AddTag(tag))
    }

    fun onRemoveTagClicked(tag: Tag) {
        tagSubject.onNext(RemoveTag(tag))
    }
}

private sealed class TagAction {
    abstract fun apply(oldTags: Set<Tag>): Set<Tag>
}

private data class AddTag(val tag: Tag) : TagAction() {
    override fun apply(oldTags: Set<Tag>): Set<Tag> {
        val tagSet = mutableSetOf(tag)

        tagSet.addAll(oldTags)

        return tagSet
    }
}

private data class RemoveTag(val tag: Tag) : TagAction() {
    override fun apply(oldTags: Set<Tag>) = oldTags.minus(tag)
}