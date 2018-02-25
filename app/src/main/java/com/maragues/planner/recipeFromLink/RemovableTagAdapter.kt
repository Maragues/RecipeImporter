package com.maragues.planner.recipeFromLink

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.recipeFromLink.RemovableTagAdapter.RemovableTagViewHolder
import com.maragues.planner.ui.views.RemovableChip

/**
 * Created by miguelaragues on 25/2/18.
 */
internal class RemovableTagAdapter() : RecyclerView.Adapter<RemovableTagViewHolder>() {
    private val tagList = mutableListOf<Tag>()

    override fun getItemCount() = tagList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RemovableTagViewHolder(RemovableChip(parent.context))

    override fun onBindViewHolder(holder: RemovableTagViewHolder, position: Int) = holder.bind(tagList[position])

    fun updateList(newTagList: List<Tag>) {
        val diff = DiffUtil.calculateDiff(TagDiffCallback(newTagList, tagList))

        tagList.clear()
        tagList.addAll(newTagList)

        diff.dispatchUpdatesTo(this)
    }

    class RemovableTagViewHolder(removableChip: RemovableChip) : RecyclerView.ViewHolder(removableChip) {
        val chip: RemovableChip = removableChip

        fun bind(tag: Tag) {
            chip.text = tag.name
        }
    }

    private class TagDiffCallback(val newTagList: List<Tag>, val oldTagList: List<Tag>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = areContentsTheSame(oldItemPosition, newItemPosition)

        override fun getOldListSize() = oldTagList.size

        override fun getNewListSize() = newTagList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldTagList[oldItemPosition] == newTagList[newItemPosition]
    }
}