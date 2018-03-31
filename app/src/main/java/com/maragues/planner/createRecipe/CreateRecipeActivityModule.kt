package com.maragues.planner.createRecipe

import android.arch.lifecycle.ViewModelProviders
import com.maragues.planner.di.FragmentScope
import com.maragues.planner.createRecipe.addTag.AddTagDialogFragment
import com.maragues.planner.createRecipe.addTag.AddTagDialogFragment.TagSelectedListener
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.annotation.Nullable

@Module(includes = [RecipesFromLinkFragmentsModule::class])
abstract class CreateRecipeActivityModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Nullable
        fun providesUrl(activity: CreateRecipeActivity): String? {
            return activity.getUrlToParse()
        }

        @JvmStatic
        @Provides
        internal fun providesTagSelectedListener(
                activity: CreateRecipeActivity,
                viewModelFactory: CreateRecipeViewModel.Factory): TagSelectedListener = ViewModelProviders.of(activity, viewModelFactory).get(CreateRecipeViewModel::class.java)
    }
}

@Module
abstract class RecipesFromLinkFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun contributeAddTagDialogFragment(): AddTagDialogFragment
}
