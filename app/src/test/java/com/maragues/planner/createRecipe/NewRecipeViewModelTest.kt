package com.maragues.planner.createRecipe

import com.maragues.planner.interactors.RecipeInteractor
import com.maragues.planner.test.BaseUnitTest
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.subjects.PublishSubject

/**
 * Created by miguelaragues on 18/2/18.
 */
class NewRecipeViewModelTest : BaseUnitTest() {
    private lateinit var viewModel: CreateRecipeViewModel

    private val urlToScrapSubject: PublishSubject<String> = PublishSubject.create()
    private val scrapper: RecipeLinkScrapper = mock()
    private val recipeInteractor: RecipeInteractor = mock()
    private val navigator: RecipeFromLinkNavigator = mock()

    /*override fun setUp() {
        super.setUp()

        viewModel = spy(NewRecipeViewModel(urlToScrapSubject, scrapper, recipeInteractor, navigator))
    }

    @Test
    fun subscribeToViewStateObservable_invokesSubscribeToUrlToScrapObservable() {
        doNothing().whenever(viewModel).subscribeToUrlToScrapObservable()

        viewModel.viewStateObservable().test()

        verify(viewModel).subscribeToUrlToScrapObservable()
    }

    *//*
    SUBSCRIBE TO URL TO SCRAP OBSERVABLE
     *//*

    @Test
    fun subscribeToUrlToScrapObservable_subscribesToUrlToScrapObservable() {
        assertFalse(urlToScrapSubject.hasObservers())

        viewModel.subscribeToUrlToScrapObservable()

        assertTrue(urlToScrapSubject.hasObservers())
    }

    @Test
    fun subscribeToUrlToScrapObservable_urlToScrapEmits_invokesScrapRecipe() {
        doNothing().whenever(viewModel).scrapRecipe(any())

        viewModel.subscribeToUrlToScrapObservable()

        verify(viewModel, never()).scrapRecipe(any())

        val expectedUrl = "blbl"
        urlToScrapSubject.onNext(expectedUrl)

        verify(viewModel).scrapRecipe(expectedUrl)
    }

    *//*
    SCRAP RECIPE
     *//*

    @Test
    fun scrapRecipe_invokesScrapeForUrl() {
        val scrappedRecipe: ScrapedRecipe = mock()
        whenever(scrapper.scrape(any())).thenReturn(Single.just(scrappedRecipe))

        doNothing().whenever(viewModel).onRecipeScrapped(scrappedRecipe)

        val expectedUrl = "blbl"
        viewModel.scrapRecipe(expectedUrl)

        verify(scrapper).scrape(expectedUrl)
    }

    @Test
    fun scrapRecipe_success_invokesOnRecipeScrapped() {
        val expectedTitle = "expected"
        val expectedUrl = "blbl"
        val expectedRecipe = ScrapedRecipe(expectedTitle, "image", expectedUrl, "description")

        whenever(scrapper.scrape(expectedUrl)).thenReturn(Single.just(expectedRecipe))

        doNothing().whenever(viewModel).onRecipeScrapped(any())

        viewModel.scrapRecipe(expectedUrl)

        verify(viewModel).onRecipeScrapped(expectedRecipe)
    }

    *//*
    ON RECIPE SCRAPPED
     *//*

    @Test
    fun onRecipeScrapped_emitsViewState() {
        val expectedUrl = "blbl"
        val recipe = ScrapedRecipe(title = "a title", image = "an image", link = expectedUrl, description = "my description")
        val expectedViewState = CreateRecipeViewState(recipe)

        doNothing().whenever(viewModel).scrapRecipe(expectedUrl)

        val observer = viewModel.viewStateObservable().test()
        observer.assertEmpty()

        viewModel.onRecipeScrapped(recipe)

        observer.assertValue(expectedViewState)
    }*/
}