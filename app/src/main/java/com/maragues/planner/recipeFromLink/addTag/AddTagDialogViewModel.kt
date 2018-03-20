package com.maragues.planner.recipeFromLink.addTag

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.text.Editable
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.repositories.TagRepository
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by miguelaragues on 3/3/18.
 */
internal class AddTagDialogViewModel(val tagRepository: TagRepository) : BaseViewModel() {

    fun viewStateObservable(filterObservable: Observable<String>): Observable<AddTagViewState> {
        return filterObservable
                .startWith("")
                .flatMap { filter ->
                    tagRepository.listFilteredBy(filter)
                            .toObservable()
                            .map { AddTagViewState(it, !filter.isEmpty()) }
                }
    }

    class Factory
    @Inject constructor(private val tagRepository: TagRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddTagDialogViewModel(tagRepository) as T
        }
    }
}



