package com.maragues.planner.recipeFromLink

import android.support.annotation.VisibleForTesting
import android.support.design.chip.Chip
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.maragues.planner.common.inflate
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner_kotlin.R
import io.reactivex.subjects.PublishSubject

/**
 * Created by miguelaragues on 25/2/18.
 */
internal class RemovableTagAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_ADD_TAG = 1
        const val VIEW_TYPE_TAG = 2
    }

    private val addTagClickSubject: PublishSubject<Unit> = PublishSubject.create()
    private val removeTagClickSubject: PublishSubject<Tag> = PublishSubject.create()

    fun addTagObservable() = addTagClickSubject.hide()
    fun removeTagObservable() = removeTagClickSubject.hide()

    private val tagList = mutableListOf<Tag>()

    override fun getItemCount() = tagList.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEW_TYPE_ADD_TAG

        return VIEW_TYPE_TAG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD_TAG -> AddTagViewHolder(parent.inflate(R.layout.item_add_tag) as Chip)

            else -> RemovableTagViewHolder(parent.inflate(R.layout.item_removable_tag) as Chip)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RemovableTagViewHolder) {
            val tag = tagList[position - 1]
            holder.bind(tag)

            holder.chip.setOnCloseIconClickListener { removeTagClickSubject.onNext(tag) }
        } else {
            (holder as AddTagViewHolder).chip.setOnClickListener { addTagClickSubject.onNext(Unit) }
        }
    }

    fun updateList(newTagList: Set<Tag>) {
        val diff = DiffUtil.calculateDiff(TagDiffCallback(newTagList.toList(), tagList))

        tagList.clear()
        tagList.addAll(newTagList)

        diff.dispatchUpdatesTo(this)
    }

    internal class RemovableTagViewHolder(val chip: Chip) : RecyclerView.ViewHolder(chip) {
        fun bind(tag: Tag) {
            chip.chipText = tag.name
        }
    }

    internal class AddTagViewHolder(val chip: Chip) : RecyclerView.ViewHolder(chip)

    @VisibleForTesting
    internal class TagDiffCallback(private val newTagList: List<Tag>, private val oldTagList: List<Tag>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = areContentsTheSame(oldItemPosition, newItemPosition)

        override fun getOldListSize() = oldTagList.size + 1

        override fun getNewListSize() = newTagList.size + 1

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (oldItemPosition == 0 || newItemPosition == 0) return oldItemPosition == newItemPosition

            return oldTagList[oldItemPosition - 1] == newTagList[newItemPosition - 1]
        }
    }
}