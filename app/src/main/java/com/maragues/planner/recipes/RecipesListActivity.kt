package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.ui.recyclerView.SpacesItemDecoration
import com.maragues.planner_kotlin.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_recipes_list.recipeList
import javax.inject.Inject

class RecipesListActivity : BaseActivity(), HasSupportFragmentInjector {

    companion object {
        fun createIntentAfterAddingRecipe(context: Context): Intent {
            return Intent(context, RecipesListActivity::class.java)
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    @Inject
    lateinit var viewModelFactory: RecipesListViewModel.Factory

    private lateinit var viewModel: RecipesListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_list)

        initDragAndrDropViews()

        initRecipesList()

        subscribeToViewModel()

//        weekMenuDragAndDrop.setOnDragListener(DragListener())
    }

    private fun initDragAndrDropViews() {
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipesListViewModel::class.java)
        disposables().add(viewModel.viewStateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { viewState -> render(viewState) },
                        { error -> error.printStackTrace() }
                ))
    }

    private fun initRecipesList() {
        recipeList.layoutManager = GridLayoutManager(this, 2)
        recipeList.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.recipe_list_grid_spacing)));
    }

    private fun render(viewState: RecipesListViewState) {
        recipeList.adapter = RecipesAdapter(viewState.recipes) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        }
    }
}
