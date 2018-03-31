package com.maragues.planner.createRecipe

import org.junit.Test

class RecipeUrlSanitizerTest {
    @Test
    fun sanitize_urlGoogleAmp() {
        val expectedUrl = "http://www.recetasderechupete.com/cocido-madrileno/6531"
        val fullUrl = "https://www.google.es/amp/amp.recetasderechupete.com/cocido-madrileno/6531"

//        assertEquals(expectedUrl, RecipeUrlSanitizer().sanitize(fullUrl))
    }

    @Test
    fun sanitize_anotherUrlGoogleAmp() {
        val expectedUrl = "https://www.elconfidencial.com/alma-corazon-vida/2016-02-18/como-hacer-el-cocido-madrileno-perfecto-en-tres-vuelcos_1154015/"
        val fullUrl = "https://www.google.es/amp/s/www.elconfidencial.com/amp/alma-corazon-vida/2016-02-18/como-hacer-el-cocido-madrileno-perfecto-en-tres-vuelcos_1154015/"

//        assertEquals(expectedUrl, RecipeUrlSanitizer().sanitize(fullUrl))
    }
}