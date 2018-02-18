package com.maragues.planner.recipeFromLink

import org.jsoup.Jsoup
import java.util.regex.Pattern
import javax.inject.Inject


class RecipeUrlSanitizer
@Inject constructor() {
    fun sanitize(url: String): String {
        return GoogleAmpSanitizer().realUrl(url)
    }
}

private class GoogleAmpSanitizer {

    private val GOOGLE_AMP_REGEX = Pattern.compile("https://www.google.*?(?:[a-z][a-z]+)\\/amp\\/(s/|amp\\.).*",
            Pattern.DOTALL or Pattern.CASE_INSENSITIVE)

    /**
     * If the url is a google amp url (https://developers.google.com/amp/), returns the real url
     * hidden behind. In order to do that, it establishes a real connection and reads the propery
     * <link rel="canonical" href="REAL_URL"/>, as specified https://www.ampproject.org/docs/guides/discovery
     *
     * Otherwise, it returns the url parameter
     */
    internal fun realUrl(url: String): String {
        val match = GOOGLE_AMP_REGEX.matcher(url)

        if (!match.matches())
            return url

        return extractRealUrl(url)
    }

    private fun extractRealUrl(url: String): String {
        val connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
        val document = connection.get()

        //see https://stackoverflow.com/questions/32892483/jsoup-with-useragent-prevent-redirects

        return document.select("link[rel=canonical]").attr("href")
    }
}