package com.maragues.planner.createRecipe

/**
 * Created by miguelaragues on 6/1/18.
 */
internal data class ScrapedRecipe(val title: String, val link: String, val image: String, val description: String) {
    companion object {
        val EMPTY = ScrapedRecipe("", "", "", "")
    }

    fun withLink(link: String): ScrapedRecipe {
        return ScrapedRecipe(title, link, image, description)
    }
}