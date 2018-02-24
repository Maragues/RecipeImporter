package com.maragues.planner.recipeFromLink

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.maragues.planner_kotlin.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_fragment_single_field.singleFieldEditText

/**
 * Created by miguelaragues on 18/2/18.
 */
class SingleFieldDialogFragment : DialogFragment() {

    companion object {
        private val ARG_URL = "arg_url"

        fun newInstance(url: String?): SingleFieldDialogFragment {
            val fragment = SingleFieldDialogFragment()

            val bundle = Bundle()
            bundle.putString(ARG_URL, url)
            fragment.arguments = bundle

            return fragment
        }
    }

    private val fieldSubject: PublishSubject<CharSequence> = PublishSubject.create()

    fun fieldObservable(): Observable<CharSequence> {
        return fieldSubject.hide()
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val dialog = AlertDialog.Builder(activity!!)
                .setView(createView())
                .setPositiveButton(R.string.single_field_dialog_save,
                        { _, _ ->
                            emitField()

                            dismiss()
                        })
                .create()

        fillField()

        return dialog
    }

    private fun fillField() {
        val fieldInitialValue = arguments?.getString(ARG_URL)

        singleFieldEditText.setText(fieldInitialValue)
    }

    private fun createView(): View {
        return LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_single_field, null, false)
    }

    private fun emitField() {
        fieldSubject.onNext(singleFieldEditText.text)
    }
}