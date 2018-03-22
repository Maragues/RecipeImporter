package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.DragEvent
import android.view.View
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.recipeFromLink.NewRecipeActivity
import com.maragues.planner.recipeFromLink.addTag.AddTagDialogFragment
import com.maragues.planner.ui.recyclerView.SpacesItemDecoration
import com.maragues.planner_kotlin.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_recipes_list.recipesListFab
import kotlinx.android.synthetic.main.activity_recipes_list.toolbar
import kotlinx.android.synthetic.main.content_recipes_list.recipeList
import kotlinx.android.synthetic.main.content_recipes_list.recipeListFilterTag
import kotlinx.android.synthetic.main.content_recipes_list.recipeListFilterTagGroup
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
    internal lateinit var viewModelFactory: RecipesListViewModel.Factory

    private lateinit var viewModel: RecipesListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_list)
        setSupportActionBar(toolbar)

        initFAB()

        initDragAndrDropViews()

        initRecipesList()

        subscribeToViewModel()

        initTagFilter()
    }

    private fun initTagFilter() {
        recipeListFilterTag.setOnClickListener {
            viewModel.onTagFilterClicked()
        }
    }

    private fun initFAB() {
        recipesListFab.setOnDragListener({ view: View, dragEvent: DragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    view.visibility = View.GONE

                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    view.visibility = View.VISIBLE

                    true
                }
                else -> {
                    false
                }
            }
        })

        recipesListFab.setOnClickListener({
            startActivity(NewRecipeActivity.createIntent(this))
        })
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
        recipeList.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.recipe_list_grid_spacing)))
    }

    private fun render(viewState: RecipesListViewState) {
        renderRecipes(viewState)

        renderTagFilter(viewState)

        renderAction(viewState)
    }

    private fun renderAction(viewState: RecipesListViewState) {
        when (viewState.actionId) {
            ACTION_SHOW_FILTER_TAG_DIALOG -> showAddTagDialog()
            ACTION_NONE -> { //do nothing
            }
        }
    }

    private fun showAddTagDialog() {
        if (supportFragmentManager.findFragmentByTag(AddTagDialogFragment.TAG) == null) {
            val addTagDialog = AddTagDialogFragment.newInstance()

            addTagDialog.show(supportFragmentManager, AddTagDialogFragment.TAG)
        }
    }

    private fun renderTagFilter(viewState: RecipesListViewState) {
        recipeListFilterTagGroup.removeAllViews()
        viewState.tagFilters.forEach({ tag ->
            val chip = Chip(this)

            chip.isCloseIconEnabled = true
            chip.chipText = tag.name
            chip.setOnCloseIconClickListener { viewModel.onTagRemoved(tag) }

            recipeListFilterTagGroup.addView(chip)
        })
    }

    private fun renderRecipes(viewState: RecipesListViewState) {
        recipeList.adapter = RecipesAdapter(viewState.recipes) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        }
    }
}
