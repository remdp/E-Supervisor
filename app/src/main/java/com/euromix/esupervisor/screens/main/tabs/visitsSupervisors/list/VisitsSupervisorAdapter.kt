package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.setIcon
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemVisitsSupervisorsListFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData

class VisitsSupervisorAdapter(
    private val onChangeMarkClick: (id: String) ->Unit,
    private val onItemClick: (direction: NavDirections) -> Unit
) :
    ListAdapter<VisitSupervisor, VisitsSupervisorAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemVisitsSupervisorsListFragmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {

            if (currentItem.showMark && currentItem.canBeRepeated) cbMark.visible() else cbMark.gone()
            cbMark.setIcon(currentItem.mark)
            cbMark.setOnClickListener { onChangeMarkClick(currentItem.extId) }

            root.setBackgroundColor(
                if (currentItem.isCheckIn && !currentItem.isCheckOut) root.context.getColor(
                    R.color.blue_5
                ) else 0
            )

            root.setBackgroundColor(if (currentItem.isDone) root.context.getColor(R.color.gray_done) else Color.TRANSPARENT)

            vHorizontalTopBlueLine.setBackgroundColor(
                if (currentItem.isDone) root.context.getColor(
                    R.color.white
                ) else root.context.getColor(R.color.gray_200)
            )
            vHorizontalBottomBlueLine.setBackgroundColor(
                if (currentItem.isDone) root.context.getColor(
                    R.color.white
                ) else root.context.getColor(R.color.gray_200)
            )

            tvPartner.text = currentItem.partner
            tvPartner.setCompoundDrawablesWithIntrinsicBounds(
                when (currentItem.fieldVisitType) {
                    0 -> if (currentItem.isCheckIn && !currentItem.isCheckOut) R.drawable.ic_check_list else R.drawable.ic_check_list_blue
                    1 -> if (currentItem.isCheckIn && !currentItem.isCheckOut) R.drawable.ic_education else R.drawable.ic_education_blue
                    else -> 0
                },
                0,
                0,
                0
            )
            tvPartner.setTextColor(
                if (currentItem.isCheckIn && !currentItem.isCheckOut) root.context.getColor(
                    R.color.blue
                ) else root.context.getColor(R.color.gray_400)
            )

            ivCheck.visibility(currentItem.outletVerified)
            ivCheck.setImageResource(if (currentItem.isCheckIn && !currentItem.isCheckOut) R.drawable.ic_check_gray_round else R.drawable.ic_check_blue_round)
            tvOutlet.text = currentItem.outlet

            root.setOnClickListener {
                onItemClick(
                    VisitsSupervisorListFragmentDirections.actionVisitsSupervisorsListFragmentToVisitSupervisorDetailFragment(
                        id = currentItem.extId,
                        titleData = TitleData(currentItem.partner)
                    )
                )
            }
        }

    }

    inner class ItemViewHolder(val binding: ItemVisitsSupervisorsListFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<VisitSupervisor>() {
        override fun areItemsTheSame(oldItem: VisitSupervisor, newItem: VisitSupervisor) =
            oldItem.extId == newItem.extId

        override fun areContentsTheSame(oldItem: VisitSupervisor, newItem: VisitSupervisor) =
            oldItem == newItem
    }
}