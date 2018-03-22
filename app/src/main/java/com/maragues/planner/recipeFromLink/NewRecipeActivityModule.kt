package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModelProviders
import com.maragues.planner.di.FragmentScope
import com.maragues.planner.recipeFromLink.addTag.AddTagDialogFragment
import com.maragues.planner.recipeFromLink.addTag.AddTagDialogFragment.TagSelectedListener
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.annotation.Nullable

@Module(includes = [RecipesFromLinkFragmentsModule::class])
abstract class NewRecipeActivityModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Nullable
        fun providesUrl(activity: NewRecipeActivity): String? {
            return activity.getUrlToParse()
        }

        @JvmStatic
        @Provides
        internal fun providesTagSelectedListener(
                activity: NewRecipeActivity,
                viewModelFactory: NewRecipeViewModel.Factory): TagSelectedListener = ViewModelProviders.of(activity, viewModelFactory).get(NewRecipeViewModel::class.java)
    }
}

@Module
abstract class RecipesFromLinkFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun contributeAddTagDialogFragment(): AddTagDialogFragment
}
