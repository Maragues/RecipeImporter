package com.maragues.planner.recipeFromLink

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.common.loadUrl
import com.maragues.planner.recipeFromLink.RecipeFromLinkNavigator.Companion.NAVIGATE_TO_RECIPE_LIST_AND_FINISH
import com.maragues.planner.recipes.RecipesListActivity
import com.maragues.planner.ui.utils.ProgressFragmentDialog
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.id.newRecipeRoot
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.newRecipeDescription
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.newRecipeImage
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.newRecipeRoot
import kotlinx.android.synthetic.main.activity_new_recipe_from_link.newRecipeTitle
import javax.inject.Inject

/**
 * Created by miguelaragues on 6/1/18.
 */
class NewRecipeActivity : BaseActivity() {

    @Inject
    internal lateinit var viewModelFactory: NewRecipeViewModel.Factory

    private lateinit var viewModel: NewRecipeViewModel

    @Inject
    lateinit var navigator: RecipeFromLinkNavigator

    companion object {
        fun createIntent(@NonNull context: Context): Intent {
            return Intent(context, NewRecipeActivity::class.java)
        }

        fun createIntentAndParseLink(@NonNull context: Context, @NonNull url: CharSequence): Intent {
            val intent = createIntent(context)

            intent.putExtra(EXTRA_TEXT, url)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe_from_link)

        subscribeToViewModel()

        subscribeToNavigator()

        initViews()
    }

    private fun initViews() {
        newRecipeTitle.setOnTouchListener({ _: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (newRecipeTitle.right - newRecipeTitle.compoundDrawables[2].bounds.width())) {
                    viewModel.userClickedUrlIcon()

                    true
                }
            }

            false
        })
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewRecipeViewModel::class.java)
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

    private fun render(viewState: CreateRecipeViewState) {
        renderScrapInProgress(viewState)

        renderScrapError(viewState)

        renderScrapedRecipe(viewState)
    }

    private fun renderScrapedRecipe(viewState: CreateRecipeViewState) {
        val scrapedRecipe = viewState.scrapedRecipe
        newRecipeTitle.setText(scrapedRecipe.title)
        val urlToLoad = if (scrapedRecipe.image.isEmpty()) null else scrapedRecipe.image
        newRecipeImage.loadUrl(urlToLoad)
        newRecipeDescription.setText(scrapedRecipe.description)

        if (!scrapedRecipe.link.isEmpty())
            tintLinkCompoundDrawable()
    }

    private fun renderScrapError(viewState: CreateRecipeViewState) {
        if (!viewState.errorLoadingScrappedRecipe.isEmpty()) {
            Snackbar.make(newRecipeRoot, viewState.errorLoadingScrappedRecipe, LENGTH_LONG).show()
        }
    }

    private fun renderScrapInProgress(viewState: CreateRecipeViewState) {
        if (viewState.scrapInProgress) {
            showScrapInProgress()
        } else {
            hideScrapInProgress()
        }
    }

    private var progressDialog: ProgressFragmentDialog? = null

    private fun showScrapInProgress() {
        if (findProgressDialogDisplayed() == null) {
            progressDialog = ProgressFragmentDialog.newInstance()
            progressDialog?.show(supportFragmentManager, ProgressFragmentDialog.TAG)
        }
    }

    private fun hideScrapInProgress() {
        progressDialog?.dismiss()
    }

    private fun findProgressDialogDisplayed() =
            supportFragmentManager.findFragmentByTag(ProgressFragmentDialog.TAG) as DialogFragment?

    private fun tintLinkCompoundDrawable() {
        val linkColor = newRecipeTitle.linkTextColors.defaultColor
        newRecipeTitle.compoundDrawables[2].setColorFilter(linkColor, PorterDuff.Mode.SRC_IN)
    }


    fun getUrlToParse(): String? {
        return intent.getStringExtra(EXTRA_TEXT)
    }
}
