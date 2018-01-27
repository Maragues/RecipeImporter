package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.ui.recyclerView.SpacesItemDecoration
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.layout
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_recipes_list.recipeList
import kotlinx.android.synthetic.main.activity_recipes_list.weekMenuDragAndDrop
import javax.inject.Inject

class RecipesListActivity : BaseActivity() {
    companion object {
        fun createIntentAfterAddingRecipe(context: Context): Intent {
            return Intent(context, RecipesListActivity::class.java)
        }
    }

    @Inject
    lateinit var viewModelFactory: RecipesListViewModel.Factory

    private lateinit var viewModel: RecipesListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_recipes_list)

        initDragAndrDropViews()

        initRecyclerView()

        subscribeToViewModel()

//        weekMenuDragAndDrop.setOnDragListener(DragListener())
    }

    class DragListener : OnDragListener{
        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

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

    private fun initRecyclerView() {
        recipeList.layoutManager = GridLayoutManager(this, 2)
        recipeList.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.recipe_list_grid_spacing)));
    }

    private fun render(viewState: RecipesListViewState) {
        recipeList.adapter = RecipesAdapter(viewState.recipes) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
        }
    }
}
