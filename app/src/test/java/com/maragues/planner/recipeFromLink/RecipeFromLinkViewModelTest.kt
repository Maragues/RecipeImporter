package com.maragues.planner.recipeFromLink

import com.maragues.planner.interactors.RecipeInteractor
import com.maragues.planner.test.BaseUnitTest
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.eq
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class RecipeFromLinkViewModelTest : BaseUnitTest() {
    lateinit var viewModel: RecipeFromLinkViewModel

    @Mock lateinit var scrapper: RecipeLinkScrapper
    @Mock lateinit var navigator: RecipeFromLinkNavigator
    @Mock lateinit var interactor: RecipeInteractor

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
        viewModel.scrapRecipe()

        verify(scrapper).scrape(eq(DEFAULT_URL))
    }

    @Test
    fun scrapRecipe_success_invokesOnRecipeScrapped() {
        val expectedTitle = "expected"
        val expectedRecipe = ScrappedRecipe(expectedTitle, "image", DEFAULT_URL, "description")

        `when`(scrapper.scrape(DEFAULT_URL)).thenReturn(Single.just(expectedRecipe))

        doNothing().`when`(viewModel).onRecipeScrapped(any(ScrappedRecipe::class.java))

        viewModel.scrapRecipe()

        verify(viewModel).onRecipeScrapped(ArgumentMatchers.eq(expectedRecipe))
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