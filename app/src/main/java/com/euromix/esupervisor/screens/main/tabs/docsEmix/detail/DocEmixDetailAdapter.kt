package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.databinding.ItemRowDocEmixBinding

class DocEmixDetailAdapter :
    ListAdapter<RowTradeCondition, DocEmixDetailAdapter.DocEmixDetailViewHolder>(ACTION_COMPARATOR) {

    companion object {
        private val ACTION_COMPARATOR = object :
            DiffUtil.ItemCallback<RowTradeCondition>() {
            override fun areItemsTheSame(oldItem: RowTradeCondition, newItem: RowTradeCondition) =
                oldItem.row == newItem.row

            override fun areContentsTheSame(
                oldItem: RowTradeCondition,
                newItem: RowTradeCondition
            ) =
                oldItem.row == newItem.row
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocEmixDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowDocEmixBinding.inflate(inflater, parent, false)
        return DocEmixDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocEmixDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
        val rowTradeCondition = (getItem(position))

        with(holder.binding) {
            tvManufacturer.text = rowTradeCondition.manufacturer
            tvPriceIndex.text = rowTradeCondition.priceIndex.toString()
            tvPaymentDeferment.text = rowTradeCondition.paymentDeferment.toString()
            tvDistributionChannel.text = rowTradeCondition.distributionChannel
        }

    }

    inner class DocEmixDetailViewHolder(val binding: ItemRowDocEmixBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentRow: RowTradeCondition) {
            with(binding) {
                tvManufacturer.text = currentRow.manufacturer
                tvPriceIndex.text = currentRow.priceIndex.toString()
                tvPaymentDeferment.text = currentRow.paymentDeferment.toString()
                tvDistributionChannel.text = currentRow.distributionChannel
            }

        }

    }
}
