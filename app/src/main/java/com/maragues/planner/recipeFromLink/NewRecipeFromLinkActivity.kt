package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.common.loadUrl
import com.maragues.planner.recipeFromLink.RecipeFromLinkNavigator.Companion.NAVIGATE_TO_RECIPE_LIST_AND_FINISH
import com.maragues.planner.recipes.RecipesListActivity
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.id.scrappedRecipeImage
import com.maragues.planner_kotlin.R.id.scrappedRecipeTitle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.scrappedRecipeDescription
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.scrappedRecipeImage
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.scrappedRecipeTitle
import javax.inject.Inject

/**
 * Created by miguelaragues on 6/1/18.
 */
class NewRecipeFromLinkActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: RecipeFromLinkViewModel.Factory

    private lateinit var viewModel: RecipeFromLinkViewModel

    @Inject
    lateinit var navigator: RecipeFromLinkNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe_from_link)

        subscribeToViewModel()

        subscribeToNavigator()
    }

    private fun subscribeToNavigator() {
        disposables().add(navigator.navigationIdObservable()
                .subscribe(
                        this::handleNavigation,
                        Throwable::printStackTrace
                ))
    }

    private fun handleNavigation(navigateAction: RecipeFromLinkNavigator.NavigateAction) {
        when (navigateAction.navigateId) {
            NAVIGATE_TO_RECIPE_LIST_AND_FINISH -> navigateToRecipeListAndFinish()
        }
    }

    private fun navigateToRecipeListAndFinish() {
        startActivity(RecipesListActivity.createIntentAfterAddingRecipe(this))

        finish()
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeFromLinkViewModel::class.java)
        disposables().add(viewModel.viewStateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::render,
                        Throwable::printStackTrace
                ))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_recipe_from_link, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_recipe_from_link_save -> {
                viewModel.onSaveClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun render(viewState: RecipeFromLinkViewState) {
        scrappedRecipeTitle.text = viewState.scrappedRecipe.title
        scrappedRecipeImage.loadUrl(viewState.scrappedRecipe.image)
        scrappedRecipeDescription.text = viewState.scrappedRecipe.description
    }


    fun getUrlToParse(): String {
        return intent.getStringExtra(EXTRA_TEXT)
    }

    fun getRecipeTitle(): String {
        return intent.getStringExtra(EXTRA_SUBJECT)
    }
}

