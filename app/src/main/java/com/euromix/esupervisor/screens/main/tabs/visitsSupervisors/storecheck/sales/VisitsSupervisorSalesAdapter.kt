package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckSale
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.databinding.ItemStoreCheckSaleBinding
import javax.inject.Inject

class VisitsSupervisorSalesAdapter @Inject constructor(private val resManager: ResourceManager) :
    ListAdapter<StoreCheckSale, VisitsSupervisorSalesAdapter.ItemViewHolder>(DiffCallBack()) {

//    @Inject
//    lateinit var resManager: ResourceManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemStoreCheckSaleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)
       // val context = holder.itemView.context

        with(holder.binding) {

            Glide.with(root.context)
                .load(currentItem.imgUrl)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(ivPicture)

            tvProduct.text = currentItem.product
            tvSalesAmount.text = resManager.getString(R.string.sum_hryvnia, currentItem.sum.toString())
        }
    }


    inner class ItemViewHolder(val binding: ItemStoreCheckSaleBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallBack : DiffUtil.ItemCallback<StoreCheckSale>() {
        override fun areItemsTheSame(
            oldItem: StoreCheckSale,
            newItem: StoreCheckSale
        ) = oldItem.product == newItem.product

        override fun areContentsTheSame(
            oldItem: StoreCheckSale,
            newItem: StoreCheckSale
        ) = oldItem == newItem

    }


}