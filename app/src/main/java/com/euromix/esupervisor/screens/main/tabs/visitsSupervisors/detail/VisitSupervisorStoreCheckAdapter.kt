package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.StoreCheckRow
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.databinding.ItemStoreCheckBinding

class VisitSupervisorStoreCheckAdapter(private val toStoreCheckDetail: (storeCheckRow: StoreCheckRow) -> Unit) :
    ListAdapter<StoreCheckRow, VisitSupervisorStoreCheckAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemStoreCheckBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {

            tvStoreCheck.text =
                root.context.getString(
                    R.string.number_date,
                    currentItem.number,
                    currentItem.date.toText()
                )

            tvStoreCheckEnd.setOnClickListener { toStoreCheckDetail(currentItem) }
        }
    }

    inner class ItemViewHolder(val binding: ItemStoreCheckBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<StoreCheckRow>() {
        override fun areItemsTheSame(oldItem: StoreCheckRow, newItem: StoreCheckRow) =
            oldItem.number == newItem.number

        override fun areContentsTheSame(oldItem: StoreCheckRow, newItem: StoreCheckRow) =
            oldItem == newItem
    }
}