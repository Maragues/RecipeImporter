package com.maragues.planner.recipeFromLink.addTag

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.text.Editable
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.repositories.TagRepository
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by miguelaragues on 3/3/18.
 */
internal class AddTagDialogViewModel(val tagRepository: TagRepository) : BaseViewModel() {

    val selectedTagSubject: PublishSubject<Tag> = PublishSubject.create();

    fun viewStateObservable(filterObservable: Observable<String>): Observable<AddTagViewState> {
        return filterObservable
                .flatMap { filter ->
                    tagRepository.listFilteredBy(filter)
                            .toObservable()
                            .map { AddTagViewState(it, !filter.isEmpty()) }
                }
                .startWith(AddTagViewState(listOf(), true))
    }

    class Factory
    @Inject constructor(private val tagRepository: TagRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddTagDialogViewModel(tagRepository) as T
        }
    }

    fun selectedTagObservable(): Observable<Tag> {
        return selectedTagSubject.hide()
    }

    fun onCreateTagClicked(text: Editable) {
        onTagSelected(Tag(text.toString()))
    }

    fun onTagSelected(tag: Tag) {
        selectedTagSubject.onNext(tag)
    }
}



