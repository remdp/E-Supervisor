package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReactionsRow
import com.euromix.esupervisor.databinding.ItemImageBinding
import com.euromix.esupervisor.databinding.ItemImagesBinding
import com.euromix.esupervisor.databinding.ItemImagesHeaderBinding
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity

class ImagesAdapter(
    private val imageOnClickListener: (imageUri: String) -> Unit,
    private val dislikeOnClickListener: (reaction: ImageReactionRequestEntity) -> Unit,
    private val likeOnClickListener: (reaction: ImageReactionRequestEntity) -> Unit
) : ListAdapter<ImagesAdapterItem, RecyclerView.ViewHolder>(ImagesDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ImagesAdapterItem.Header -> TYPE_HEADER
            is ImagesAdapterItem.ImageRow -> TYPE_ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                HeaderViewHolder(ItemImagesHeaderBinding.inflate(inflater, parent, false))
            }

            else -> {
                ImagesRowViewHolder(
                    ItemImagesBinding.inflate(inflater, parent, false),
                    imageOnClickListener,
                    dislikeOnClickListener,
                    likeOnClickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ImagesAdapterItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ImagesAdapterItem.ImageRow -> (holder as ImagesRowViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(private val binding: ItemImagesHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImagesAdapterItem.Header) {
            binding.tvHeader.text = item.title

            val topPaddingRes = if (item.hasExtraTopSpace) {
                R.dimen.DP_48
            } else {
                R.dimen.DP_16
            }

            binding.tvHeader.setPadding(
                binding.tvHeader.paddingLeft,
                itemView.context.resources.getDimensionPixelSize(topPaddingRes),
                binding.tvHeader.paddingRight,
                binding.tvHeader.paddingBottom
            )
        }
    }

    class ImagesRowViewHolder(
        private val binding: ItemImagesBinding,
        private val imageClick: (String) -> Unit,
        private val dislikeClick: (ImageReactionRequestEntity) -> Unit,
        private val likeClick: (ImageReactionRequestEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImagesAdapterItem.ImageRow) {
            val context = itemView.context

            renderItem(context, item.leftImage, binding.incLeft, item.isEditable)

            if (item.rightImage == null) {
                binding.incRight.clRoot.visibility =
                    View.INVISIBLE
            } else {
                binding.incRight.clRoot.visibility = View.VISIBLE
                renderItem(context, item.rightImage, binding.incRight, item.isEditable)
            }
        }

        private fun renderItem(
            context: Context,
            reaction: ImageReactionsRow,
            binding: ItemImageBinding,
            isEditable: Boolean
        ) {
            with(binding) {
                Glide.with(context)
                    .load(reaction.path)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                    .into(ivPic)

                val controlsVisibility = if (isEditable) View.VISIBLE else View.GONE

                ivBgDislike.visibility = controlsVisibility
                ivBgLike.visibility = controlsVisibility
                ivDislike.visibility = controlsVisibility
                ivLike.visibility = controlsVisibility
                ivDislikeCounterBg.visibility = controlsVisibility
                tvCounterDislikes.visibility = controlsVisibility
                ivLikeCounterBg.visibility = controlsVisibility
                tvCounterLikes.visibility = controlsVisibility

                clRoot.setBackgroundResource(
                    if (!isEditable) R.drawable.bg_8dp_border_gray_200 // Для СВ всегда серая рамка
                    else if (reaction.dislikes == 0 && reaction.likes == 0) R.drawable.bg_8dp_border_gray_200
                    else if (reaction.dislikes > 0 && reaction.likes > 0) R.drawable.bg_8dp_border_gradient
                    else if (reaction.likes > 0) R.drawable.bg_8dp_border_blue
                    else R.drawable.bg_8dp_border_red_light
                )

                if (isEditable) {
                    ivDislike.setImageResource(if (reaction.dislikes > 0) R.drawable.ic_dislike_red else R.drawable.ic_dislike_gray_400)
                    ivLike.setImageResource(if (reaction.likes > 0) R.drawable.ic_like_blue else R.drawable.ic_like_gray_400)

                    if (reaction.likes == 0) {
                        ivLikeCounterBg.visibility = View.GONE
                        tvCounterLikes.visibility = View.GONE
                    } else {
                        ivLikeCounterBg.visibility = View.VISIBLE
                        tvCounterLikes.visibility = View.VISIBLE
                        tvCounterLikes.text = reaction.likes.toString()
                    }

                    if (reaction.dislikes == 0) {
                        ivDislikeCounterBg.visibility = View.GONE
                        tvCounterDislikes.visibility = View.GONE
                    } else {
                        ivDislikeCounterBg.visibility = View.VISIBLE
                        tvCounterDislikes.visibility = View.VISIBLE
                        tvCounterDislikes.text = reaction.dislikes.toString()
                    }

                    if (reaction.dislikes > 0) {
                        tvDislikeReason.visibility = View.VISIBLE
                        tvDislikeReason.text = reaction.comment
                    } else {
                        tvDislikeReason.visibility = View.GONE
                    }

                    ivBgDislike.setOnClickListener {
                        dislikeClick(ImageReactionRequestEntity(url = reaction.path, reaction = 1))
                    }
                    ivBgLike.setOnClickListener {
                        likeClick(ImageReactionRequestEntity(url = reaction.path, reaction = 0))
                    }
                } else {
                    ivBgDislike.setOnClickListener(null)
                    ivBgLike.setOnClickListener(null)
                    tvDislikeReason.visibility = View.GONE
                }

                ivPic.setOnClickListener { imageClick(reaction.path) }
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ROW = 1
    }
}

class ImagesDiffCallback : DiffUtil.ItemCallback<ImagesAdapterItem>() {
    override fun areItemsTheSame(oldItem: ImagesAdapterItem, newItem: ImagesAdapterItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ImagesAdapterItem,
        newItem: ImagesAdapterItem
    ): Boolean {
        return oldItem == newItem
    }
}

sealed interface ImagesAdapterItem {
    data class Header(val title: String, val hasExtraTopSpace: Boolean = false) : ImagesAdapterItem

    data class ImageRow(
        val leftImage: ImageReactionsRow,
        val rightImage: ImageReactionsRow?,
        val isEditable: Boolean
    ) : ImagesAdapterItem
}