package com.maragues.planner

import android.app.Application
import com.maragues.planner.di.DaggerAppComponent
import dagger.android.HasActivityInjector
import android.app.Activity
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import timber.log.Timber
import javax.inject.Inject


class App : Application(), HasActivityInjector {

    @Inject
    internal lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    internal lateinit var tree: Timber.Tree

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this);

        Stetho.initializeWithDefaults(this)

        initDagger()

        Timber.plant(tree)
    }

    private fun initDagger() {
        DaggerAppComponent.builder()
                .context(this)
                .build()
                .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}