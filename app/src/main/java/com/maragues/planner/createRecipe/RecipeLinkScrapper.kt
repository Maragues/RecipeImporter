package com.maragues.planner.createRecipe

import io.reactivex.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import javax.inject.Inject

/**
 * Created by miguelaragues on 6/1/18.
 */
class RecipeLinkScrapper
@Inject constructor(val urlSanitizer: RecipeUrlSanitizer) {
    internal fun scrape(url: String): Single<ScrapedRecipe> {
        return Single.fromCallable({
            parseRecipe(urlSanitizer.sanitize(url))
        })
    }

    private fun parseRecipe(url: String): ScrapedRecipe {
        val connection = Jsoup.connect(url)
        val document = connection.get()

        val metaOgTitle = readOgProperty(document, "title")
        var title: String;
        if (metaOgTitle != null && metaOgTitle.hasAttr("content")) {
            title = metaOgTitle.attr("content")
        } else {
            title = document.title()
        }

        var imageUrl = ""
        val metaOgImage = readOgProperty(document, "image")
        if (metaOgImage != null) {
            imageUrl = metaOgImage.attr("content")
        }

        var description = ""
        val metaOgDescription = readOgProperty(document, "description")
        if (metaOgDescription != null) {
            description = metaOgDescription.attr("content")
        }

        return ScrapedRecipe(title, url, imageUrl, description)
    }

    private fun readOgProperty(document: Document, property: String): Elements? {
        return document.select("meta[property=og:$property]")
    }

    private fun defaultUserAgent() = System.getProperty("http.agent")
}