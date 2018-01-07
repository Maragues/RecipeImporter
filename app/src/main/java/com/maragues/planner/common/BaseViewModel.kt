package com.maragues.planner.common

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by miguelaragues on 7/1/18.
 */
abstract class BaseViewModel : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()

        disposables.dispose()
    }

    protected fun disposables(): CompositeDisposable {
        return disposables
    }
}