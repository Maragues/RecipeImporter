package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import com.maragues.planner.common.BaseFragment
import com.maragues.planner.common.setVisible
import com.maragues.planner.createRecipe.CreateRecipeActivity
import com.maragues.planner.createRecipe.addTag.AddTagDialogFragment
import com.maragues.planner.recipes.hoveringPlanner.HoveringPlannerRecyclerView
import com.maragues.planner.showRecipe.ShowRecipeActivity
import com.maragues.planner.ui.recyclerView.SpacesItemDecoration
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.dimen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_recipes_list.recipesListFab
import kotlinx.android.synthetic.main.content_recipes_list.recipeList
import kotlinx.android.synthetic.main.content_recipes_list.recipeListFilterTagGroup
import kotlinx.android.synthetic.main.content_recipes_list.recipeListRoot
import kotlinx.android.synthetic.main.content_recipes_list.recipeListTagsFiltered
import javax.inject.Inject

class RecipesListFragment : BaseFragment(), HasSupportFragmentInjector {

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

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recipes_list, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFAB()

        initDragAndDropViews()

        initRecipesList()

        swapZIndexOnDragEvents()

        subscribeToViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater?.inflate(R.menu.recipes_list_menu, menu)

        initSearchView(menu.findItem(R.id.search_recipes))
    }

    private fun initSearchView(searchViewItem: MenuItem) {
        with(searchViewItem.actionView as SearchView) {
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    viewModel.onSearchRecipe(queryText)

                    return true
                }
            })

            setOnCloseListener {
                viewModel.onSearchRecipe("")

                false
            }

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                run {
                    if (hasFocus) {
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm?.showSoftInput(view.findFocus(), InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }

            queryHint = getString(R.string.search_hint)
        }

        searchViewItem.setOnActionExpandListener(object : OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                (menuItem.actionView as SearchView).isIconified = false

                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                (menuItem.actionView as SearchView).isIconified = true
                (menuItem.actionView as SearchView).setQuery("", false)
                viewModel.onSearchRecipe("")

                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_recipes -> {
                viewModel.onTagFilterClicked()

                true
            }
//            android.R.id.home -> viewModel.onSearchRecipe("")
        }

        return super.onOptionsItemSelected(item)
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
            startActivity(CreateRecipeActivity.createIntent(context!!))
        })
    }

    private fun initDragAndDropViews() {
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
        recipeList.layoutManager = GridLayoutManager(context, 2)
        recipeList.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(dimen.recipe_list_grid_spacing)))
    }

    private fun swapZIndexOnDragEvents() {
        recipeListRoot.setOnDragListener { view, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    val fragment = childFragmentManager.findFragmentById(R.id.plannerFragment)
                    recipeListRoot.bringChildToFront(fragment.view)
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    view.postDelayed({
                        recipeListRoot.bringChildToFront(recipeList)
                    }, HoveringPlannerRecyclerView.FADE_OUT_DELAY_MILLIS)
                }
            }

            true
        }
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
        if (childFragmentManager.findFragmentByTag(AddTagDialogFragment.TAG) == null) {
            val addTagDialog = AddTagDialogFragment.newInstance()

            addTagDialog.show(childFragmentManager, AddTagDialogFragment.TAG)
        }
    }

    private fun renderTagFilter(viewState: RecipesListViewState) {
        recipeListTagsFiltered.setVisible(!viewState.tagFilters.isEmpty())

        recipeListFilterTagGroup.removeAllViews()
        viewState.tagFilters.forEach({ tag ->
            val chip = Chip(context)

            chip.isCloseIconEnabled = true
            chip.chipText = tag.name
            chip.setOnCloseIconClickListener { viewModel.onTagRemoved(tag) }

            recipeListFilterTagGroup.addView(chip)
        })
    }

    private fun renderRecipes(viewState: RecipesListViewState) {
        recipeList.adapter = RecipesAdapter(viewState.recipes) {
            startActivity(ShowRecipeActivity.createIntent(context!!, it))
        }
    }
}