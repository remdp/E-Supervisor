package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.topLevel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevelRow
import com.euromix.esupervisor.databinding.ItemStoreCheckTopLevelBinding


class StoreCheckTopLevelAdapter(
    private val onSalesViewClick: (item: StoreCheckTopLevelRow) -> Unit,
    private val onItemClick: (item: StoreCheckTopLevelRow) -> Unit
) :
    ListAdapter<StoreCheckTopLevelRow, StoreCheckTopLevelAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemStoreCheckTopLevelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {
            tvTopLevel.text = currentItem.serverObject.serverPair.presentation

            tvTopLevel.setCompoundDrawablesWithIntrinsicBounds(
                0, 0,
                if (currentItem.sales.isNotEmpty()) R.drawable.ic_store_check_sale else 0,
                0
            )
            root.setOnClickListener { onItemClick(currentItem) }

            tvTopLevel.setOnTouchListener { v, event ->

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val textLocation = IntArray(2)
                        v.getLocationOnScreen(textLocation)

                        if (event.rawX >= textLocation[0] + tvTopLevel.width - tvTopLevel.totalPaddingRight) {
                            // Right drawable was tapped
                            onSalesViewClick(currentItem)
                            return@setOnTouchListener true
                        }
                    }
                }
                return@setOnTouchListener false
            }

        }
    }


    inner class ItemViewHolder(val binding: ItemStoreCheckTopLevelBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<StoreCheckTopLevelRow>() {
        override fun areItemsTheSame(
            oldItem: StoreCheckTopLevelRow,
            newItem: StoreCheckTopLevelRow
        ) =
            oldItem.serverObject.serverPair.id == newItem.serverObject.serverPair.id


        override fun areContentsTheSame(
            oldItem: StoreCheckTopLevelRow,
            newItem: StoreCheckTopLevelRow
        ) =
            oldItem == newItem
    }

}