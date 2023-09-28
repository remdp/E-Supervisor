package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.ImageReactionsRow
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.invisible
import com.euromix.esupervisor.app.utils.isEven
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemImageBinding
import com.euromix.esupervisor.databinding.ItemImagesBinding
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity

class ImagesAdapter(
    private val imageOnClickListener: (imageUri: String) -> Unit,
    private val dislikeOnClickListener: (reaction: ImageReactionRequestEntity) -> Unit,
    private val likeOnClickListener: (reaction: ImageReactionRequestEntity) -> Unit
) :
    RecyclerView.Adapter<ImagesAdapter.ImagesVIewHolder>() {

    private var imagesRvList: MutableList<Pair<ImageReactionsRow, ImageReactionsRow?>> =
        mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesVIewHolder =
        ImagesVIewHolder(
            ItemImagesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ImagesVIewHolder, position: Int) {

        val imagesElement = imagesRvList[position]

        with(holder) {

            renderItemLike(itemView.context, imagesElement.first, binding.incLeft)

            if (imagesElement.second == null) {
                binding.incRight.clRoot.invisible()
            } else {
                renderItemLike(itemView.context, imagesElement.second!!, binding.incRight)
            }
        }
    }

    private fun renderItemLike(
        context: Context,
        reaction: ImageReactionsRow,
        binding: ItemImageBinding
    ) {

        with(binding) {
            Glide.with(context).load(reaction.path).into(ivPic)

            clRoot.setBackgroundResource(
                if (reaction.dislikes == 0 && reaction.likes == 0) R.drawable.bg_border_gray_200
                else if (reaction.dislikes > 0 && reaction.likes > 0) R.drawable.bg_border_gradient
                else if (reaction.likes > 0) R.drawable.bg_border_blue
                else R.drawable.bg_border_red
            )

            ivDislike.setImageResource(if (reaction.dislikes > 0) R.drawable.ic_dislike_red else R.drawable.ic_dislike_gray_400)
            ivLike.setImageResource(if (reaction.likes > 0) R.drawable.ic_like_blue else R.drawable.ic_like_gray_400)

            if (reaction.likes == 0) {
                ivLikeCounterBg.gone()
                tvCounterLikes.gone()
            } else {
                ivLikeCounterBg.visible()
                tvCounterLikes.visible()
            }
            tvCounterLikes.text = reaction.likes.toString()

            if (reaction.dislikes == 0) {
                ivDislikeCounterBg.gone()
                tvCounterDislikes.gone()
            } else {
                ivDislikeCounterBg.visible()
                tvCounterDislikes.visible()
            }
            tvCounterDislikes.text = reaction.dislikes.toString()

            if (reaction.dislikes > 0) {
                tvDislikeReason.visible()
                tvDislikeReason.text = reaction.comment
            } else tvDislikeReason.gone()

            ivPic.setOnClickListener { imageOnClickListener(reaction.path) }
            ivBgDislike.setOnClickListener {
                dislikeOnClickListener(
                    ImageReactionRequestEntity(
                        url = reaction.path,
                        reaction = 1
                    )
                )
            }
            ivBgLike.setOnClickListener {
                likeOnClickListener(
                    ImageReactionRequestEntity(
                        url = reaction.path,
                        reaction = 0
                    )
                )
            }
        }
    }

    fun setImages(imagesReactions: List<ImageReactionsRow>) {

        imagesRvList.clear()
        if (imagesReactions.isNotEmpty()) {
            for (i in imagesReactions.indices) {
                if (isEven(i)) {
                    imagesRvList.add(
                        Pair(
                            imagesReactions[i],
                            if (i + 1 < imagesReactions.size) imagesReactions[i + 1] else null
                        )
                    )
                }
            }
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = imagesRvList.size

    inner class ImagesVIewHolder(val binding: ItemImagesBinding) :
        RecyclerView.ViewHolder(binding.root)

}