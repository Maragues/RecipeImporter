package com.maragues.planner.recipeFromLink.addTag

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.maragues.planner.common.BaseDaggerDialogFragment
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.recipeFromLink.NewRecipeActivity
import com.maragues.planner.recipeFromLink.NewRecipeViewModel
import com.maragues.planner_kotlin.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_add_tag.addTagCreate
import kotlinx.android.synthetic.main.dialog_add_tag.addTagFilter
import kotlinx.android.synthetic.main.dialog_add_tag.addTagRecyclerView
import javax.inject.Inject

/**
 * Created by miguelaragues on 3/3/18.
 */
internal class AddTagDialogFragment : BaseDaggerDialogFragment() {

    companion object {
        val TAG = AddTagDialogFragment::class.java.simpleName

        fun newInstance(): AddTagDialogFragment {
            return AddTagDialogFragment()
        }
    }

    @Inject
    internal lateinit var activityViewModelFactory: NewRecipeViewModel.Factory

    @Inject
    internal lateinit var viewModelFactory: AddTagDialogViewModel.Factory

    private lateinit var viewModel: AddTagDialogViewModel

    private lateinit var activityViewModel: NewRecipeViewModel

    private val tagAdapter = TagAdapter {
        activityViewModel.onTagSelected(it)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddTagDialogViewModel::class.java)
        activityViewModel = ViewModelProviders.of(this, activityViewModelFactory).get(NewRecipeViewModel::class.java)

        return AlertDialog.Builder(context!!)
                .setTitle(R.string.add_tag_dialog_title)
                .setCancelable(true)
                .setView(inflateLayout())
                .create()
    }

    lateinit var createButton: Button
    lateinit var emptyLayout: View

    private fun inflateLayout(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_tag, null, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.addTagRecyclerView)
        createButton = view.findViewById(R.id.addTagCreate)
        emptyLayout = view.findViewById(R.id.addTagEmptyTags)

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = tagAdapter

        createButton.setOnClickListener {
            viewModel.onCreateTagClicked(addTagFilter.text)
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        disposables().add(
                viewModel.viewStateObservable(filterObservable())
                        .subscribe(this::render, Throwable::printStackTrace)
        )
    }

    private fun filterObservable(): Observable<String> {
        val filterSubject: PublishSubject<String> = PublishSubject.create()

        view?.findViewById<TextView>(R.id.addTagFilter)?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                filterSubject.onNext(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return filterSubject
    }

    private fun render(viewState: AddTagViewState) {
        tagAdapter.updateList(viewState.filteredTags)

        emptyLayout.visibility = if (viewState.filteredTags.isEmpty()) View.VISIBLE else View.GONE

        createButton.isEnabled = viewState.createEnabled
    }
}