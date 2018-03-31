package com.maragues.planner.createRecipe.addTag

import android.support.design.chip.Chip
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.maragues.planner.common.inflate
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.createRecipe.addTag.TagAdapter.TagViewHolder
import com.maragues.planner_kotlin.R

/**
 * Created by miguelaragues on 25/2/18.
 */
internal class TagAdapter(private val listener: (Tag) -> Unit) : RecyclerView.Adapter<TagViewHolder>() {
    private val tagList = mutableListOf<Tag>()

    override fun getItemCount() = tagList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TagViewHolder(parent.inflate(R.layout.item_tag) as Chip)

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) = holder.bind(tagList[position], listener)

    fun updateList(newTagList: List<Tag>) {
        val diff = DiffUtil.calculateDiff(TagDiffCallback(newTagList, tagList))

        tagList.clear()
        tagList.addAll(newTagList)

        diff.dispatchUpdatesTo(this)
    }

    class TagViewHolder(val chip: Chip) : RecyclerView.ViewHolder(chip) {
        fun bind(tag: Tag, listener: (Tag) -> Unit) = with(chip) {
            chipText = tag.name

            setOnClickListener { listener(tag) }
        }
    }

    private class TagDiffCallback(val newTagList: List<Tag>, val oldTagList: List<Tag>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = areContentsTheSame(oldItemPosition, newItemPosition)

        override fun getOldListSize() = oldTagList.size

        override fun getNewListSize() = newTagList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldTagList[oldItemPosition] == newTagList[newItemPosition]
    }
}