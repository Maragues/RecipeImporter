package com.maragues.planner.recipeFromLink

import java.util.regex.Pattern
import javax.inject.Inject
import java.util.regex.Pattern.DOTALL


class RecipeUrlSanitizer
@Inject constructor() {
    val GOOGLE_AMP_REGEX = Pattern.compile("google.*?(?:[a-z][a-z]+)\\/amp\\/amp\\.",
            Pattern.DOTALL or Pattern.CASE_INSENSITIVE)

    fun sanitize(url: String): String {
        return removeGoogleAmp(url)
    }

    private fun removeGoogleAmp(url: String): String {
        return GOOGLE_AMP_REGEX.matcher(url).replaceAll("");
    }
}