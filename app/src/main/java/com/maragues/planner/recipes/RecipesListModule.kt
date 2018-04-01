package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModelProviders
import com.maragues.planner.createRecipe.addTag.AddTagDialogFragment.TagSelectedListener
import dagger.Module
import dagger.Provides

@Module(includes = [RecipesListFragmentsModule::class])
class RecipesListModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        internal fun providesTagSelectedListener(
                recipesListFragment: RecipesListFragment,
                viewModelFactory: RecipesListViewModel.Factory): TagSelectedListener {
            return ViewModelProviders.of(recipesListFragment, viewModelFactory).get(RecipesListViewModel::class.java)
        }
    }
}