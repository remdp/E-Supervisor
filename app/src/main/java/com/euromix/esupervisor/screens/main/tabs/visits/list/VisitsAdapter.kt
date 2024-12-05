package com.euromix.esupervisor.screens.main.tabs.visits.list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.routes.entities.MapPointSigns
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.app.utils.bitmapFromDrawableRes
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.setIcon
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemVisitsListFragmentBinding

class VisitsAdapter(
    val context: Context,
    val onMarkClick: (extId: String) -> Unit,
    val onChangeVisitTypeClick: (extId: String, visitType: VisitType) -> Unit
) :
    ListAdapter<Visit, VisitsAdapter.ItemViewHolder>(DiffCallback()) {

    inner class ItemViewHolder(val binding: ItemVisitsListFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            ItemVisitsListFragmentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {
            tvPartner.text = currentItem.partner

            MapPointSigns.drawableResForSigns(
                outletTA = true,
                isVisit = true,
                checkInBounds = currentItem.checkInBounds,
                isVisitDone = currentItem.done,
                isOrders = currentItem.orderSum != 0f,
                isDistanceVisitOutlet = currentItem.type == VisitType.REMOTE
            ).let { drawableRes ->

                tvDate.text = textDate(currentItem.date)
                tvNumber.text = currentItem.number

                val drawable = BitmapDrawable(
                    context.resources, Bitmap.createScaledBitmap(
                        context.bitmapFromDrawableRes(drawableRes) ?: return@let, 80, 80, true
                    )
                )
                tvPartner.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }

            tvAddress.text = currentItem.address
            if (currentItem.unscheduled) ivUnscheduled.visible() else ivUnscheduled.gone()

            if (currentItem.checkIn.isEmpty() && currentItem.outletTime.isEmpty() && currentItem.orderSum == 0f && currentItem.cashReceiptOrderSum == 0f) iVisitStatistic.root.gone()
            else {
                iVisitStatistic.tvVisitTime.text = currentItem.checkIn
                iVisitStatistic.tvVisitDuration.text = currentItem.outletTime
                iVisitStatistic.tvOrderSum.text = currentItem.orderSum.toString()
                iVisitStatistic.tvCashReceiptOrderSum.text =
                    currentItem.cashReceiptOrderSum.toString()
                iVisitStatistic.root.visible()
            }

            if (currentItem.showMark && currentItem.canBeChanged) cbMark.visible() else cbMark.gone()
            cbMark.setIcon(currentItem.mark)
            cbMark.setOnClickListener {
                onMarkClick(currentItem.extId)
            }

            root.isSwipeEnabled = currentItem.canBeChanged

            if (currentItem.type == VisitType.REMOTE) {
                ivChangeVisitType.setImageResource(R.drawable.ic_visit_regular)
                llIv.setBackgroundColor(context.getColor(R.color.blue_80))
            } else {
                ivChangeVisitType.setImageResource(R.drawable.ic_visit_remote)
                llIv.setBackgroundColor(context.getColor(R.color.orange_80))
            }

            ivChangeVisitType.setOnClickListener {
                onChangeVisitTypeClick(
                    currentItem.extId,
                    if (currentItem.type == VisitType.REMOTE) VisitType.REGULAR else VisitType.REMOTE
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Visit>() {
        override fun areItemsTheSame(oldItem: Visit, newItem: Visit) =
            oldItem.extId == newItem.extId

        override fun areContentsTheSame(oldItem: Visit, newItem: Visit) = oldItem == newItem
    }
}