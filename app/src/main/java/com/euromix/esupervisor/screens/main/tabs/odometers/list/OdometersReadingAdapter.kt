package com.euromix.esupervisor.screens.main.tabs.odometers.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.euromix.esupervisor.app.utils.isTimeZero
import com.euromix.esupervisor.app.utils.setBitmapFromBase64String
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.app.utils.toTimeString
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.ItemOdometersReadingBinding

class OdometersReadingAdapter() :
    ListAdapter<OdometersReading, OdometersReadingAdapter.ItemViewHolder>(DiffCallback()) {

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
            tvDate.text = textDate(currentItem.date)
            tvDriver.text = currentItem.driver
            tvMileage.text = if (currentItem.mileage > 0) currentItem.mileage.toString() else ""
            tvStartTime.text =
                if (!currentItem.startTime.isTimeZero()) currentItem.startTime.toTimeString() else ""
            currentItem.stopTime?.let { if (!it.isTimeZero()) tvStopTime.text = it.toTimeString() }

            ivStartPhoto.setBitmapFromBase64String(currentItem.startPhoto)
            ivStopPhoto.setBitmapFromBase64String(currentItem.stopPhoto)

            ivStartPhotoStatus.visibility(currentItem.startPhoto != null)
            ivStopPhotoStatus.visibility(currentItem.stopPhoto != null)

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OdometersReading>() {
        override fun areItemsTheSame(oldItem: OdometersReading, newItem: OdometersReading) =
            oldItem.date == newItem.date && oldItem.driver == newItem.driver

        override fun areContentsTheSame(oldItem: OdometersReading, newItem: OdometersReading) =
            oldItem == newItem
    }
}