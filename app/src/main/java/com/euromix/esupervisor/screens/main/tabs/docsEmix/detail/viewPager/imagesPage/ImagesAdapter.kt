package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.databinding.ItemImagesBinding

class ImagesAdapter(private val imageOnClickListener: (imageUri: String) -> Unit) :
    RecyclerView.Adapter<ImagesAdapter.ImagesVIewHolder>() {

    private var imagesRvList: MutableList<MutableList<String>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesVIewHolder =
        ImagesVIewHolder(
            ItemImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ImagesVIewHolder, position: Int) {

        val imagesElement = imagesRvList[position]

        with(holder) {

            val leftImageUri = imagesElement[0]
            Glide.with(itemView.context).load(leftImageUri).into(binding.ivLeft)
            binding.ivLeft.setOnClickListener { imageOnClickListener(leftImageUri) }

            if (imagesElement.size > 1){
                val rightImageUri = imagesElement[1]
                Glide.with(itemView.context).load(rightImageUri).into(binding.ivRight)
                binding.ivRight.setOnClickListener { imageOnClickListener(rightImageUri) }
            }
        }
    }

    fun setImages(fragmentArgs: Bundle) {

        for (i in 1..fragmentArgs.size()) {
            fragmentArgs.parcelable<String>(PATH + i)?.let {

                if (imagesRvList.isNotEmpty()) {
                    val lastElement = imagesRvList.last()
                    if (lastElement.size == 2) imagesRvList.add(mutableListOf(it))
                    else lastElement.add(it)
                } else {
                    imagesRvList.add(mutableListOf(it))
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = imagesRvList.size

    inner class ImagesVIewHolder(val binding: ItemImagesBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val PATH = "PATH"
    }
}