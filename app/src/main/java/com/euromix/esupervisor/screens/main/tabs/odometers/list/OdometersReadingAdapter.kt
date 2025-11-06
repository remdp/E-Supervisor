package com.euromix.esupervisor.screens.main.tabs.odometers.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReadingItem
import com.euromix.esupervisor.app.utils.setBitmapFromBase64String
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.toTextHMS
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.ItemOdometersReadingBinding

class OdometersReadingAdapter() :
    ListAdapter<OdometersReadingItem, OdometersReadingAdapter.ItemViewHolder>(DiffCallback()) {

    inner class ItemViewHolder(val binding: ItemOdometersReadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemOdometersReadingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {
            tvDate.text = currentItem.date.toText()
            tvDriver.text = currentItem.driver
            tvCarNumber.text = currentItem.carNumber
            tvMileage.text = if (currentItem.mileage > 0) currentItem.mileage.toString() else ""
            tvStartKm.text = currentItem.startKm
            tvStopKm.text = currentItem.stopKm
            tvStartTime.text = currentItem.startTime.toTextHMS()
            tvStopTime.text = currentItem.stopTime?.toTextHMS("")

            ivStartPhoto.setBitmapFromBase64String(currentItem.startPhoto)
            ivStopPhoto.setBitmapFromBase64String(currentItem.stopPhoto)

            ivStartPhotoStatus.visibility(currentItem.startPhoto != null)
            ivStopPhotoStatus.visibility(currentItem.stopPhoto != null)

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OdometersReadingItem>() {
        override fun areItemsTheSame(oldItem: OdometersReadingItem, newItem: OdometersReadingItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: OdometersReadingItem,
            newItem: OdometersReadingItem
        ) =
            oldItem == newItem
    }
}