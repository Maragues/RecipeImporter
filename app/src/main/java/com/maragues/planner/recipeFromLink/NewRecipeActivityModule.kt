package com.maragues.planner.recipeFromLink

import com.maragues.planner.di.FragmentScope
import com.maragues.planner.recipeFromLink.addTag.AddTagDialogFragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.annotation.Nullable

@Module(includes = [RecipesFromLinkFragmentsModule::class])
class NewRecipeActivityModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Nullable
        fun providesUrl(activity: NewRecipeActivity): String? {
            return activity.getUrlToParse()
        }
    }
}

@Module
abstract class RecipesFromLinkFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun contributeAddTagDialogFragment(): AddTagDialogFragment
}
