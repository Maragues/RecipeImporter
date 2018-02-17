package com.maragues.planner.recipeFromLink

import com.maragues.planner.interactors.RecipeInteractor
import com.maragues.planner.test.BaseUnitTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Test

class RecipeFromLinkViewModelTest : BaseUnitTest() {
    lateinit var viewModel: RecipeFromLinkViewModel

    val scrapper: RecipeLinkScrapper = mock()

    val navigator: RecipeFromLinkNavigator = mock()

    val interactor: RecipeInteractor = mock()

    val DEFAULT_URL = "ignorable"

    override fun setUp() {
        super.setUp()

        viewModel = spy(RecipeFromLinkViewModel(DEFAULT_URL, scrapper, interactor, navigator))
    }

    @Test
    fun subscribe_invokesScrapRecipe() {
        doNothing().`when`(viewModel).scrapRecipe();

        viewModel.viewStateObservable().test()

        verify(viewModel).scrapRecipe()
    }

    /*
    SCRAP RECIPE
     */

    @Test
    fun scrapRecipe_invokesScrapeForUrl() {
        val scrappedRecipe: ScrappedRecipe = mock()
        whenever(scrapper.scrape(any())).thenReturn(Single.just(scrappedRecipe))

        doNothing().`when`(viewModel).onRecipeScrapped(scrappedRecipe)

        viewModel.scrapRecipe()

        verify(scrapper).scrape(DEFAULT_URL)
    }

    @Test
    fun scrapRecipe_success_invokesOnRecipeScrapped() {
        val expectedTitle = "expected"
        val expectedRecipe = ScrappedRecipe(expectedTitle, "image", DEFAULT_URL, "description")

        whenever(scrapper.scrape(DEFAULT_URL)).thenReturn(Single.just(expectedRecipe))

        doNothing().whenever(viewModel).onRecipeScrapped(any())

        viewModel.scrapRecipe()

        verify(viewModel).onRecipeScrapped(expectedRecipe)
    }

    /*
    ON RECUPE SCRAPPED
     */

    @Test
    fun onRecipeScrapped_emitsViewState() {
        val recipe = ScrappedRecipe(title = "a title", image = "an image", link = DEFAULT_URL, description = "my description")
        val expectedViewState = RecipeFromLinkViewState(recipe)

        doNothing().`when`(viewModel).scrapRecipe()

        val observer = viewModel.viewStateObservable().test()
        observer.assertEmpty()

        viewModel.onRecipeScrapped(recipe)

        observer.assertValue(expectedViewState)
    }
}