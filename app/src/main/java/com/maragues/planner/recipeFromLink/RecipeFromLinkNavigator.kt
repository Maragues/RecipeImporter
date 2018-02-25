package com.maragues.planner.recipeFromLink

import android.support.annotation.IntDef
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by miguelaragues on 7/1/18.
 */
@Singleton
class RecipeFromLinkNavigator
@Inject constructor() {
    private val navigationIdSubject: PublishSubject<NavigateAction> = PublishSubject.create()
    internal fun navigationIdObservable(): Observable<NavigateAction> {
        return navigationIdSubject.hide()
    }

    internal fun navigateToRecipeList() {
        navigationIdSubject.onNext(NavigateAction(NAVIGATE_TO_RECIPE_LIST_AND_FINISH))
    }

    internal data class NavigateAction(@NavigateId val navigateId: Int)

    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(EXIT, NAVIGATE_TO_RECIPE_LIST_AND_FINISH)
        internal annotation class NavigateId

        const val EXIT = 0
        const val NAVIGATE_TO_RECIPE_LIST_AND_FINISH = 1

    }
}