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
        val expectedDescription = "Te explicamos paso a paso, de manera sencilla, cómo elaborar la receta de arroz con fideos. Tiempo de elaboración, ingredientes,"
        val expectedImage = "https://i.blogs.es/4dea6c/arroz-con-fideos/840_560.jpg"
        val observer = testUrl(link)

        observer.assertValue(ScrappedRecipe(expectedTitle, link, expectedImage, expectedDescription))
    }

    private fun testUrl(url: String): TestObserver<ScrappedRecipe> {
        Mockito.`when`(sanitizer.sanitize(url)).thenReturn(url)

        return scrapper.scrape(url).test()
    }
}

// pected: ScrappedRecipe(title=Bil Shareyah o arroz con fideos. Receta de raíces egipcias, link=https://i.blogs.es/4dea6c/arroz-con-fideos/840_560.jpg, image=https://www.directoalpaladar.com/recetas-de-salsas-y-guarniciones/bil-shareyah-o-arroz-con-fideos-receta-de-raices-egipcias, description=Te explicamos paso a paso, de manera sencilla, cómo elaborar la receta de arroz con fideos. Tiempo de elaboración, ingredientes,) (class: ScrappedRecipe),
// Actual: ScrappedRecipe(title=Bil Shareyah o arroz con fideos. Receta de raíces egipcias, link=https://www.directoalpaladar.com/recetas-de-salsas-y-guarniciones/bil-shareyah-o-arroz-con-fideos-receta-de-raices-egipcias, image=https://i.blogs.es/4dea6c/arroz-con-fideos/840_560.jpg, description=Te explicamos paso a paso, de manera sencilla, cómo elaborar la receta de arroz con fideos. Tiempo de elaboración, ingredientes,)
