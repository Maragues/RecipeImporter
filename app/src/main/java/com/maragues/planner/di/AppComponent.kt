package com.maragues.planner.di

import android.content.Context
import com.maragues.planner.App
import com.maragues.planner.persistence.repositories.RepositoriesModule
import com.maragues.planner.persistence.room.RoomModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * https://github.com/googlesamples/android-architecture-components/tree/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di
 */
@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    BindingModule::class,
    RoomModule::class,
    RepositoriesModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

}