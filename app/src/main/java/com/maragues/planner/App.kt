package com.maragues.planner

import android.app.Application
import com.maragues.planner.di.DaggerAppComponent
import dagger.android.HasActivityInjector
import android.app.Activity
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject


class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this);

        //Instantiate Dagger
        DaggerAppComponent.builder()
                .context(this)
                .build()
                .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}