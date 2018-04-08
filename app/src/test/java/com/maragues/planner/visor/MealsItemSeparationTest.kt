package com.maragues.planner.visor

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.maragues.planner.test.BaseUnitTest
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt

class MealsItemSeparationTest : BaseUnitTest() {
    companion object {
        private const val SEPARATION = 4

        private const val DEFAULT_WEEKS = 3
    }

    private val mealsSeparator = MealsItemSeparation(SEPARATION)

    @Test
    fun position0_doesntTouchOutRect() {
        val outRect = mockSeparator(0, 0, 0)

        assertRectValues(outRect, 0, 0, 0, 0)
    }

    @Test
    fun positionOverZero_row0_column1_setsExpectedSeparations() {
        val outRect = mockSeparator(1, 0, 1)

        assertRectValues(outRect, leftMargin = SEPARATION, rightMargin = SEPARATION, topMargin = 0, bottomMargin = 0)
    }

    @Test
    fun positionOverZero_row1_column1_setsExpectedSeparations() {
        val outRect = mockSeparator(1, 1, 1)

        assertRectValues(outRect, leftMargin = SEPARATION, rightMargin = SEPARATION, topMargin = SEPARATION, bottomMargin = 0)
    }

    @Test
    fun positionOverZero_row0_columnLast_setsExpectedSeparations() {
        val outRect = mockSeparator(1, 0, lastColumn())

        assertRectValues(outRect, leftMargin = 0, rightMargin = 0, topMargin = 0, bottomMargin = 0)
    }

    @Test
    fun positionOverZero_row1_column0_setsExpectedSeparations() {
        val outRect = mockSeparator(1, 1, 0)

        assertRectValues(outRect, leftMargin = 0, rightMargin = 0, topMargin = SEPARATION, bottomMargin = 0)
    }

    @Test
    fun positionOverZero_row1_columnLast_setsExpectedSeparations() {
        val outRect = mockSeparator(1, 1, lastColumn())

        assertRectValues(outRect, leftMargin = 0, rightMargin = 0, topMargin = SEPARATION, bottomMargin = 0)
    }

    @Test
    fun positionOverZero_rowLast_column0_setsExpectedSeparations() {
        val outRect = mockSeparator(1, lastRow(), 0)

        assertRectValues(outRect, leftMargin = 0, rightMargin = 0, topMargin = SEPARATION, bottomMargin = 0)
    }

    @Test
    fun positionOverZero_rowLast_columnLast_setsExpectedSeparations() {
        val outRect = mockSeparator(1, lastRow(), lastColumn())

        assertRectValues(outRect, leftMargin = 0, rightMargin = 0, topMargin = SEPARATION, bottomMargin = 0)
    }

    /*
    UTILS
     */

    private fun lastColumn() = DEFAULT_WEEKS - 1
    private fun lastRow() = MealsAdapter.DAYS_IN_WEEK - 1

    private fun assertRectValues(rect: Rect, leftMargin: Int, rightMargin: Int, topMargin: Int, bottomMargin: Int) {
        assertEquals("Expected left margin $leftMargin, was ${rect.left}", leftMargin, rect.left)
        assertEquals("Expected right margin $rightMargin, was ${rect.right}", rightMargin, rect.right)
        assertEquals("Expected top margin $topMargin, was ${rect.top}", topMargin, rect.top)
        assertEquals("Expected bottom margin $bottomMargin, was ${rect.bottom}", bottomMargin, rect.bottom)
    }

    private fun mockSeparator(position: Int, row: Int, column: Int): Rect {
        val mealsAdapter = mockMealsAdapter(position, row, column)

        return mockGetItemOffsets(position, mealsAdapter)
    }

    private fun mockMealsAdapter(position: Int, row: Int, column: Int): MealsAdapter {
        val mealsAdapter: MealsAdapter = mock()

        whenever(mealsAdapter.getTotalWeeks()).thenReturn(DEFAULT_WEEKS)
        whenever(mealsAdapter.getWeekIndex(position)).thenReturn(column)
        whenever(mealsAdapter.getWeekOffset(position)).thenReturn(row)

        return mealsAdapter
    }

    private fun mockGetItemOffsets(position: Int, mealsAdapter: MealsAdapter): Rect {
        val outRect = Rect()
        val view: View = mock()
        val recyclerView: RecyclerView = mock()
        val state: android.support.v7.widget.RecyclerView.State = mock()

        whenever(recyclerView.getChildLayoutPosition(view)).thenReturn(position)

        whenever(recyclerView.adapter).thenReturn(mealsAdapter);

        mealsSeparator.getItemOffsets(outRect, view, recyclerView, state)

        return outRect
    }
}