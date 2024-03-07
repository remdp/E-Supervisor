package com.euromix.esupervisor.screens.main.tabs.visits.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.databinding.ItemImageReactionBinding
import com.euromix.esupervisor.databinding.ItemVisitsListFragmentBinding
import com.euromix.esupervisor.databinding.VisitsListFragmentBinding

class VisitsAdapter : ListAdapter<Visit, VisitsAdapter.ItemViewHolder>(DiffCallback()) {

    inner class ItemViewHolder(val binding: ItemVisitsListFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemVisitsListFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {
            tvPartner.text = currentItem.partner
            tvAddress.text = currentItem.address

            iVisitStatistic.tvVisitTime.text = currentItem.checkIn
            iVisitStatistic.tvVisitDuration.text = currentItem.outletTime
            iVisitStatistic.tvOrderSum.text = currentItem.orderSum.toString()
            iVisitStatistic.tvCashReceiptOrderSum.text = currentItem.cashReceiptOrderSum.toString()
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Visit>() {
        override fun areItemsTheSame(oldItem: Visit, newItem: Visit): Boolean =
            oldItem.extId == newItem.extId

        override fun areContentsTheSame(oldItem: Visit, newItem: Visit): Boolean =
            oldItem == newItem

    }
}