package com.maragues.planner.test

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import com.maragues.planner.test.rules.ImmediateRxSchedulersOverrideRule



/**
 * Created by miguelaragues on 6/1/18.
 */
open class BaseUnitTest {

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