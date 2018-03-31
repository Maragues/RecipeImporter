package com.maragues.planner.createRecipe

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.maragues.planner.test.BaseEspressoTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewRecipeFromLinkEspressoTest : BaseEspressoTest() {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        Assert.assertEquals("com.maragues.planner_kotlin", appContext.packageName)
    }
}