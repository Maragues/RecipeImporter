package com.maragues.planner.showRecipe

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.PorterDuff.Mode.SRC_IN
import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.common.loadUrl
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.color
import com.maragues.planner_kotlin.R.drawable
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_create_recipe.newRecipeImage
import kotlinx.android.synthetic.main.content_create_recipe.newRecipeTitle
import kotlinx.android.synthetic.main.content_recipes_list.recipeListFilterTagGroup
import kotlinx.android.synthetic.main.content_show_recipe.showRecipeDescription
import kotlinx.android.synthetic.main.content_show_recipe.showRecipeImage
import kotlinx.android.synthetic.main.content_show_recipe.showRecipeTags
import kotlinx.android.synthetic.main.content_show_recipe.showRecipeTitle
import javax.inject.Inject

class ShowRecipeActivity : BaseActivity() {

    companion object {
        private const val EXTRA_RECIPE_ID = "extra_recipe_id"

        fun createIntent(context: Context, recipe: Recipe): Intent {
            val intent = Intent(context, ShowRecipeActivity::class.java)

            intent.putExtra(EXTRA_RECIPE_ID, recipe.id)

            return intent
        }
    }

    @Inject
    internal lateinit var viewModelFactory: ShowRecipeViewModel.Factory

    private lateinit var viewModel: ShowRecipeViewModel

    internal fun recipeId(): Long {
        return intent.getLongExtra(EXTRA_RECIPE_ID, -1L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)

        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShowRecipeViewModel::class.java)
        disposables().add(viewModel.viewStateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::render,
                        Throwable::printStackTrace
                ))
    }

    fun render(viewState: ShowRecipeViewState) {
        with(viewState.recipe) {
            showRecipeTitle.text = title
            showRecipeDescription.text = description

            val urlToLoad = if (screenshot.isEmpty()) null else screenshot
            showRecipeImage.loadUrl(urlToLoad)

            if (!url.isEmpty()) {
                prepareLinkIconClick(url)
            }
        }

        renderTags(viewState)
    }

    private fun prepareLinkIconClick(url: String) {
        showRecipeTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawable.ic_link, 0)
        showRecipeTitle.compoundDrawables[2]
                .setColorFilter(
                        ContextCompat.getColor(this, color.linkColor),
                        SRC_IN)

        showRecipeTitle.setOnTouchListener({ _: View, event: MotionEvent ->
            var handled = false
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (event.rawX >= (showRecipeTitle.right - showRecipeTitle.compoundDrawables[2].bounds.width())) {
                    onRecipeLinkClicked(url)

                    handled = true
                }
            }

            handled
        })
    }

    private fun onRecipeLinkClicked(url: String) {
        startActivity(Intent(ACTION_VIEW, Uri.parse(url)))
    }

    private fun renderTags(viewState: ShowRecipeViewState) {
        showRecipeTags.removeAllViews()
        viewState.tags.forEach({ tag ->
            val chip = Chip(this)

            chip.chipText = tag.name

            showRecipeTags.addView(chip)
        })
    }
}
