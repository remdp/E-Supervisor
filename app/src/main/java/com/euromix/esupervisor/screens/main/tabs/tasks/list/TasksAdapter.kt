package com.euromix.esupervisor.screens.main.tabs.tasks.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.TaskState
import com.euromix.esupervisor.app.model.tasks.entities.Task
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.ItemTaskBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import java.time.LocalDate

//class TasksAdapter(
//    private val showOutlet: Boolean = true,
//    private val toTaskDetail: (task: Task) -> Unit
//) :
//    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {
//
//    var tasks: List<Task> = emptyList()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemTaskBinding.inflate(inflater, parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val task = tasks[position]
//
//        with(holder.binding) {
//
//            root.background =
//                if (showOutlet) root.context.getDrawable(R.drawable.bg_8dp_gray_light) else null
//            tvDate.text = task.date.toText()
//            tvNumber.text = task.number
//            tvExecutor.text = task.executor
//            tvTaskType.text = task.taskType
//            TaskState.designTV(tvTaskState, task.taskState)
//            tvDescription.text = task.description
//            tvDeadline.text = task.deadline.toText()
//            tvPartner.text = task.partner
//            tvPartner.visibility(showOutlet)
//            tvOutletLabel.visibility(showOutlet)
//            tvOutlet.text = task.outlet
//            tvOutlet.visibility(showOutlet)
//            tvAttachPhoto.setCompoundDrawablesWithIntrinsicBounds(
//                if (task.attachPhoto) R.drawable.ic_checkbox_on else R.drawable.ic_checkbox_off,
//                0,
//                0,
//                0
//            )
//            tvAttachPhoto.visibility(showOutlet)
//
//            tvDeadline.setTextColor(
//                root.context.getColor(
//                    if (task.deadline < LocalDate.now()) R.color.red_light else R.color.black
//                )
//            )
//            tvDeadline.setCompoundDrawablesWithIntrinsicBounds(
//                if (task.deadline < LocalDate.now()) R.drawable.ic_cross_red else 0,
//                0,
//                0,
//                0
//            )
//
//            root.setOnClickListener { toTaskDetail(task) }
//        }
//
//    }
//
//    override fun getItemCount() = tasks.size
//
//    inner class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)
//
//}

class TasksAdapter(
    private val showOutlet: Boolean = true,
    private val toTaskDetail: (task: Task) -> Unit
) : ListAdapter<Task, TasksAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)

        with(holder.binding) {
            root.background =
                if (showOutlet) root.context.getDrawable(R.drawable.bg_8dp_gray_light) else null

            tvDate.text = task.date.toText()
            tvNumber.text = task.number
            tvExecutor.text = task.executor
            tvTaskType.text = task.taskType
            TaskState.designTV(tvTaskState, task.taskState)
            tvDescription.text = task.description
            tvDeadline.text = task.deadline.toText()
            tvPartner.text = task.partner
            tvPartner.visibility(showOutlet)
            tvOutletLabel.visibility(showOutlet)
            tvOutlet.text = task.outlet
            tvOutlet.visibility(showOutlet)

            tvAttachPhoto.setCompoundDrawablesWithIntrinsicBounds(
                if (task.attachPhoto) R.drawable.ic_checkbox_on else R.drawable.ic_checkbox_off,
                0,
                0,
                0
            )
            tvAttachPhoto.visibility(showOutlet)

            val isOverdue = task.deadline < LocalDate.now()
            tvDeadline.setTextColor(
                root.context.getColor(if (isOverdue) R.color.red_light else R.color.black)
            )
            tvDeadline.setCompoundDrawablesWithIntrinsicBounds(
                if (isOverdue) R.drawable.ic_cross_red else 0,
                0,
                0,
                0
            )

            root.setOnClickListener { toTaskDetail(task) }
        }
    }

    inner class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.extId == newItem.extId

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}