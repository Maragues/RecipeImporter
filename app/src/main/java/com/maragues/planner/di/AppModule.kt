package com.maragues.planner.di

import com.maragues.planner.utils.DebugLoggingTree
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

/**
 * https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di
 * TODO: Modify dependencies here!
 */
@Module
internal class AppModule {

    @Provides
    @Singleton
    internal fun providesTree(): Timber.Tree {
        return DebugLoggingTree()
    }
}