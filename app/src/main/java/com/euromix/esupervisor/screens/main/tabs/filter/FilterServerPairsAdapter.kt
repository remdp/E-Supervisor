package com.euromix.esupervisor.screens.main.tabs.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.filter.entities.DetailFilterItem
import com.euromix.esupervisor.databinding.ItemSelectionBinding

class FilterServerPairsAdapter(
    private val itemClickListener: ((updatedItem: DetailFilterItem) -> Unit)
) : ListAdapter<DetailFilterItem, FilterServerPairsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val filterItem = getItem(position)

        with(holder.binding.checkBox) {

            text = filterItem.serverPair.presentation
            setButtonDrawable(if (filterItem.marked) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off)

            setOnClickListener {
                val updatedItem = filterItem.copy(marked = !filterItem.marked)
                itemClickListener.invoke(updatedItem)
            }
        }
    }

    inner class ViewHolder(val binding: ItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<DetailFilterItem>() {
        override fun areItemsTheSame(oldItem: DetailFilterItem, newItem: DetailFilterItem) =
            oldItem.serverPair.id == newItem.serverPair.id

        override fun areContentsTheSame(oldItem: DetailFilterItem, newItem: DetailFilterItem) =
            oldItem == newItem
    }
}