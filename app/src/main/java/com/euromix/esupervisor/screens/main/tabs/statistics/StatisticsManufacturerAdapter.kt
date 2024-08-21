package com.euromix.esupervisor.screens.main.tabs.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.invisible
import com.euromix.esupervisor.databinding.ItemStatisticManufacturersBinding
import com.euromix.esupervisor.sources.routes.entities.ManufacturerLogo

class StatisticsManufacturerAdapter :
    ListAdapter<List<ManufacturerLogo>, StatisticsManufacturerAdapter.ItemViewHolder>(
        DiffCallback()
    ) {

    inner class ItemViewHolder(val binding: ItemStatisticManufacturersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemStatisticManufacturersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {
            val includes = listOf(i1, i2, i3, i4, i5, i6)
            for (i in 0..<MANUFACTURERS_IN_ITEM) {

                val inc = includes[i]
                if (i in currentItem.indices) {
                    inc.tvManufacturer.text = currentItem[i].name
                    inc.iv.background = App.getDrawable(
                        root.context,
                        if (currentItem[i].currentSale) R.drawable.bg_4dp_white_border_blue else R.drawable.bg_4dp_white
                    )
                    Glide.with(root.context).load(currentItem[i].URL).into(inc.iv)
                } else {
                    inc.root.invisible()
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<List<ManufacturerLogo>>() {
        override fun areItemsTheSame(
            oldItem: List<ManufacturerLogo>,
            newItem: List<ManufacturerLogo>
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: List<ManufacturerLogo>,
            newItem: List<ManufacturerLogo>
        ): Boolean {
            if (oldItem.size != newItem.size) return false
            for (i in oldItem.indices) {
                if (oldItem[i] != newItem[i]) return false
            }
            return true
        }
    }

    companion object{
        const val MANUFACTURERS_IN_ITEM = 6
    }
}