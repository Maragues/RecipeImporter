package com.maragues.planner.common

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by miguelaragues on 12/2/18.
 */
abstract class BaseFragment : Fragment() {
    private val disposables = CompositeDisposable()

    protected fun disposables(): CompositeDisposable {
        return disposables
    }

    override fun onAttach(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Perform injection here before M, L (API 22) and below because onAttach(Context)
            // is not yet available at L.
            AndroidSupportInjection.inject(this)
        }
        super.onAttach(activity)
    }

    override fun onAttach(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Perform injection here for M (API 23) due to deprecation of onAttach(Activity).
            AndroidSupportInjection.inject(this)
        }
        super.onAttach(context)
    }

    override fun onPause() {
        super.onPause()

        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }
}