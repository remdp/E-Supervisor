package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.returnRequestPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.RowReturnRequest
import com.euromix.esupervisor.databinding.ItemRowReturnRequestBinding

class DocEmixDetailReturnRequestAdapter :
    ListAdapter<RowReturnRequest, DocEmixDetailReturnRequestAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemRowReturnRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRowReturnRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentRow: RowReturnRequest) {

            with(binding) {
                Glide.with(this.root).load(currentRow.imageURI).placeholder(R.drawable.ic_logo)
                    .into(ivGoods)
                tvGoods.text = currentRow.goods
                tvSeries.text = currentRow.series
                tvAmount.text = currentRow.amount.toString()
                tvUnit.text = currentRow.unit
                tvBaseDate.text = currentRow.baseDate
                tvSum.text = currentRow.sum.toString()
                tvBase.text = currentRow.base
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<RowReturnRequest>() {
            override fun areItemsTheSame(oldItem: RowReturnRequest, newItem: RowReturnRequest) =
                oldItem.row == newItem.row

            override fun areContentsTheSame(
                oldItem: RowReturnRequest,
                newItem: RowReturnRequest
            ) =
                oldItem == newItem
        }
    }
}