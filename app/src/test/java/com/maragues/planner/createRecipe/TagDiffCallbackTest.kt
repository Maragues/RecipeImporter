package com.maragues.planner.createRecipe

import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.createRecipe.RemovableTagAdapter.TagDiffCallback
import com.maragues.planner.test.BaseUnitTest
import org.junit.Assert.*
import org.junit.Test

class TagDiffCallbackTest : BaseUnitTest() {
    private lateinit var tagDiffCallback: TagDiffCallback

    @Test
    fun twoEmptyLists_returnsSize1() {
        tagDiffCallback = TagDiffCallback(listOf(), listOf())

        assertEquals(1, tagDiffCallback.oldListSize)
        assertEquals(1, tagDiffCallback.newListSize)
    }

    @Test
    fun twoListsWithContent_returnsSizePlus1() {
        val newTagList = listOf(Tag("1"))
        val oldTagList = listOf(Tag("2"), Tag("3"))
        tagDiffCallback = TagDiffCallback(newTagList, oldTagList)

        assertEquals(oldTagList.size + 1, tagDiffCallback.oldListSize)
        assertEquals(newTagList.size + 1, tagDiffCallback.newListSize)
    }

    /*
    ARE CONTENTS THE SAME
     */

    @Test
    fun areContentsTheSame_empty_new0_old0_returnsTrue() {
        tagDiffCallback = TagDiffCallback(listOf(), listOf())

        assertTrue(tagDiffCallback.areContentsTheSame(0, 0))
    }

    @Test
    fun areContentsTheSame_withContent_new0_old0_returnsTrue() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("3")))

        assertTrue(tagDiffCallback.areContentsTheSame(0, 0))
    }

    @Test
    fun areContentsTheSame_withContent_new1_old0_returnsFalse() {
        tagDiffCallback = TagDiffCallback(listOf(), listOf(Tag("3")))

        assertFalse(tagDiffCallback.areContentsTheSame(0, 1))
    }

    @Test
    fun areContentsTheSame_withContent_new0_old1_returnsFalse() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("3")))

        assertFalse(tagDiffCallback.areContentsTheSame(1, 0))
    }

    @Test
    fun areContentsTheSame_withDifferentContent_new1_old1_returnsFalse() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("3")))

        assertFalse(tagDiffCallback.areContentsTheSame(1, 1))
    }

    @Test
    fun areContentsTheSame_withEqualsContent_new1_old1_returnsTrue() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("1")))

        assertTrue(tagDiffCallback.areContentsTheSame(1, 1))
    }

    /*
    ARE ITEMS THE SAME
     */

    @Test
    fun areItemsTheSame_empty_new0_old0_returnsTrue() {
        tagDiffCallback = TagDiffCallback(listOf(), listOf())

        assertTrue(tagDiffCallback.areItemsTheSame(0, 0))
    }

    @Test
    fun areItemsTheSame_withContent_new0_old0_returnsTrue() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("3")))

        assertTrue(tagDiffCallback.areItemsTheSame(0, 0))
    }

    @Test
    fun areItemsTheSame_withContent_new1_old0_returnsFalse() {
        tagDiffCallback = TagDiffCallback(listOf(), listOf(Tag("3")))

        assertFalse(tagDiffCallback.areItemsTheSame(0, 1))
    }

    @Test
    fun areItemsTheSame_withContent_new0_old1_returnsFalse() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("3")))

        assertFalse(tagDiffCallback.areItemsTheSame(1, 0))
    }

    @Test
    fun areItemsTheSame_withDifferentContent_new1_old1_returnsFalse() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("3")))

        assertFalse(tagDiffCallback.areItemsTheSame(1, 1))
    }

    @Test
    fun areItemsTheSame_withEqualsContent_new1_old1_returnsTrue() {
        tagDiffCallback = TagDiffCallback(listOf(Tag("1")), listOf(Tag("1")))

        assertTrue(tagDiffCallback.areItemsTheSame(1, 1))
    }
}