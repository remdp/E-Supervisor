package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.utils.setBitmapFromBase64String
import com.euromix.esupervisor.databinding.ItemPhotoGalleryBinding

class PhotoGalleryAdapter(
    private val onRemoveClick: (Int) -> Unit,
    private val onImageClick:(Int, String) -> Unit
) : ListAdapter<String, PhotoGalleryAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoGalleryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding, onRemoveClick, onImageClick)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhotoViewHolder(
        private val binding: ItemPhotoGalleryBinding,
        private val onRemoveClick: (Int) -> Unit,
        private val onImageClick:(Int, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(base64String: String) {
            binding.ivGalleryPhoto.setBitmapFromBase64String(base64String)

            binding.ivGalleryPhoto.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onImageClick(position, base64String) 
                }
            }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
}