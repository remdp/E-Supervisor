package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.databinding.ItemRowDocEmixBinding

class DocEmixDetailTradeConditionAdapter :
    RecyclerView.Adapter<DocEmixDetailTradeConditionAdapter.DocEmixDetailViewHolder>() {

    var rowTradeCondition: List<RowTradeCondition> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocEmixDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowDocEmixBinding.inflate(inflater, parent, false)
        return DocEmixDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocEmixDetailViewHolder, position: Int) {
        holder.bind(rowTradeCondition[position])
    }

    override fun getItemCount(): Int = rowTradeCondition.size

    inner class DocEmixDetailViewHolder(val binding: ItemRowDocEmixBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentRow: RowTradeCondition) {
            with(binding) {
                tvManufacturer.text = currentRow.manufacturer
                tvPriceIndex.text = currentRow.priceIndex.toString()
                tvPaymentDeferment.text = currentRow.paymentDeferment.toString()
                tvDistributionChannel.text = currentRow.distributionChannel
                Glide.with(this.root).load(currentRow.imageURI).placeholder(R.drawable.ic_logo)
                    .into(ivManufacturer)
            }
        }
    }
}