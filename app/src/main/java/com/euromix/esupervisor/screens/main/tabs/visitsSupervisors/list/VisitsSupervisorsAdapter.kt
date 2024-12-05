package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.databinding.ItemVisitsSupervisorsListFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData

class VisitsSupervisorsAdapter(private val onItemClick: (direction: NavDirections) -> Unit) :
    ListAdapter<VisitSupervisor, VisitsSupervisorsAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemVisitsSupervisorsListFragmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)

        with(holder.binding) {

            tvDate.text = textDate(currentItem.date)
            tvNumber.text = currentItem.number
            tvPartner.text = currentItem.partner
            tvOutlet.text = currentItem.outlet

            root.setOnClickListener {
                onItemClick(
                    VisitsSupervisorsListFragmentDirections.actionVisitsSupervisorsListFragmentToVisitSupervisorDetailFragment(
                        id = currentItem.id,
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
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: VisitSupervisor, newItem: VisitSupervisor) =
            oldItem == newItem

    }

}