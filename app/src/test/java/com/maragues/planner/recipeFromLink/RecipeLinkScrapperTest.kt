package com.maragues.planner.recipeFromLink

import com.maragues.planner.test.BaseUnitTest
import io.reactivex.observers.TestObserver
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class RecipeLinkScrapperTest : BaseUnitTest() {
    lateinit var scrapper: RecipeLinkScrapper

    @Mock lateinit var sanitizer: RecipeUrlSanitizer

    override fun setUp() {
        super.setUp()

        scrapper = RecipeLinkScrapper(sanitizer)
    }

    @Test
    fun scrapeLink_returnsScrappedRecipeWithTitleAndLink() {
        val link = "https://www.directoalpaladar.com/recetas-de-salsas-y-guarniciones/bil-shareyah-o-arroz-con-fideos-receta-de-raices-egipcias";
        val expectedTitle = "Bil Shareyah o arroz con fideos. Receta de raíces egipcias";
        val observer = testUrl(link)

        observer.assertValue(ScrappedRecipe(expectedTitle, link))
    }

    private fun testUrl(url: String): TestObserver<ScrappedRecipe> {
        Mockito.`when`(sanitizer.sanitize(url)).thenReturn(url)

        return scrapper.scrape(url).test()
    }
}

