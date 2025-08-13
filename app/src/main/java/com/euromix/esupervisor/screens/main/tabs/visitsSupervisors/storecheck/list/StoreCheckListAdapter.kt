//package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.list
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckVisit
//import com.euromix.esupervisor.databinding.ItemStorecheckListFragmentBinding
//import com.euromix.esupervisor.screens.main.tabs.tasks.list.TasksAdapter
//
//class StoreCheckListAdapter(
//    private val onStoreCheckClick: (storeCheckId: String, storeCheckNumber: String, twoLevels: Boolean) -> Unit,
//    private val onAddTaskClick: (id: String, number: String, partner: String, outlet: String, tradeAgent: String) -> Unit
//) :
//    ListAdapter<StoreCheckVisit, StoreCheckListAdapter.ItemViewHolder>(DiffCallback()) {
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ) = ItemViewHolder(
//        ItemStorecheckListFragmentBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//    )
//
//    override fun onBindViewHolder(holder: StoreCheckListAdapter.ItemViewHolder, position: Int) {
//        val currentItem = getItem(position)
//
//        with(holder.binding) {
//
//            tvStoreCheck.text = currentItem.number
//            tvStoreCheck.setOnClickListener {
//                onStoreCheckClick(
//                    currentItem.id,
//                    currentItem.number,
//                    currentItem.twoLevels
//                )
//            }
//            ivAddTask.setOnClickListener {
//                onAddTaskClick(
//                    currentItem.id,
//                    currentItem.number,
//                    currentItem.partner,
//                    currentItem.outlet,
//                    currentItem.tradeAgent
//                )
//            }
//
//            if (currentItem.tasks.isEmpty())
//                tvAddTask.setPadding(
//                    tvAddTask.paddingLeft,
//                    tvAddTask.paddingTop,
//                    tvAddTask.paddingRight,
//                    (8 * tvAddTask.resources.displayMetrics.density).toInt()
//                )
//
////            val ad = TasksAdapter()
////            rvTasks.adapter = ad
////            ad.tasks = currentItem.tasks
//
//            rvTasks.adapter = TasksAdapter().apply {
//                tasks = currentItem.tasks
//            }
//        }
//    }
//
//    inner class ItemViewHolder(val binding: ItemStorecheckListFragmentBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//
//    class DiffCallback : DiffUtil.ItemCallback<StoreCheckVisit>() {
//        override fun areItemsTheSame(oldItem: StoreCheckVisit, newItem: StoreCheckVisit) =
//            oldItem.id == newItem.id
//
//
//        override fun areContentsTheSame(oldItem: StoreCheckVisit, newItem: StoreCheckVisit) =
//            oldItem == newItem
//
//    }
//}
//
//
//
//
//
