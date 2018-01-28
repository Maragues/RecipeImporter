package com.maragues.planner.test

import com.jakewharton.threetenabp.AndroidThreeTen.init
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import com.maragues.planner.test.rules.ImmediateRxSchedulersOverrideRule
import com.maragues.planner.test.utils.DebugLoggingTree
import timber.log.Timber


/**
 * Created by miguelaragues on 6/1/18.
 */
open class BaseUnitTest {

    private val TEST_TREE = object : DebugLoggingTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            println(message)
        }
    }

    init {
        if (Timber.treeCount() == 0) {
            Timber.plant(TEST_TREE)
        }
    }

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val mOverrideSchedulersRule = ImmediateRxSchedulersOverrideRule()

    @Before
    open fun setUp() {

    }

    @After
    open fun tearDown() {
    }
}