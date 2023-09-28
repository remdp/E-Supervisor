package com.euromix.esupervisor.screens.main.tabs.tasks.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.TaskState
import com.euromix.esupervisor.app.model.tasks.entities.Task
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.databinding.ItemTaskBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import java.time.LocalDate

class TasksAdapter : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    var tasks: List<Task> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val task = tasks[position]

        with(holder.binding) {

            tvDate.text = textDate(task.date)
            tvNumber.text = task.number
            tvExecutor.text = task.executor
            tvTaskType.text = task.taskType
            TaskState.designTV(tvTaskState, task.taskState)
            tvDescription.text = task.description
            tvDeadline.text = textDate(task.deadline)
            tvPartner.text = task.partner
            tvOutlet.text = task.outlet
            tvAttachPhoto.setCompoundDrawablesWithIntrinsicBounds(
                if (task.attachPhoto) R.drawable.ic_checkbox_on else R.drawable.ic_checkbox_off,
                0,
                0,
                0
            )

            tvDeadline.setTextColor(
                getColor(
                    root.context,
                    if (task.deadline < LocalDate.now()) R.color.red_light else R.color.black
                )
            )
            tvDeadline.setCompoundDrawablesWithIntrinsicBounds(
                if (task.deadline < LocalDate.now()) R.drawable.ic_cross_red else 0,
                0,
                0,
                0
            )
        }

        holder.binding.root.setOnClickListener {

            val direction = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(
                task.extId,
                TitleData(task.number, textDate(task.date))
            )
            it.findNavController().navigate(direction)
        }

    }

    override fun getItemCount() = tasks.size

    inner class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

}