package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReaction
import com.euromix.esupervisor.databinding.ItemImageReactionBinding

class ImageAdapter : ListAdapter<ImageReaction, ImageAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemImageReactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)

        with(holder.binding) {
            tvUser.text = currentItem.user
            tvUser.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                when (currentItem.reaction) {
                    0 -> R.drawable.ic_like_blue
                    1 -> R.drawable.ic_dislike_red
                    else -> 0
                },
                0
            )
            tvComment.text = currentItem.comment
        }
    }

    inner class ItemViewHolder(val binding: ItemImageReactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<ImageReaction>() {
        override fun areItemsTheSame(oldItem: ImageReaction, newItem: ImageReaction) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ImageReaction, newItem: ImageReaction) =
            oldItem == newItem
    }
}