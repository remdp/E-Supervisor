package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.databinding.ItemOutletCreateTaskBinding
import com.euromix.esupervisor.screens.main.tabs.tasks.selection.SelectionItemOutlet

class OutletsAdapter(
    private val viewModel: CreateTaskViewModel,
    var itemClickListener: ((position: Int, mark: Boolean) -> Unit)? = null
) : RecyclerView.Adapter<OutletsAdapter.ViewHolder>() {

    var list: List<SelectionItemOutlet> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOutletCreateTaskBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            cbPartner.text = item.outlet.owner

            cbPartner.setOnCheckedChangeListener(null)

            cbPartner.isChecked = item.marked
            cbPartner.setButtonDrawable(viewModel.drawableForChildCheckBox(item.marked))

            cbPartner.setOnClickListener {
                val mark = !item.marked
                item.marked = mark
                cbPartner.isChecked = mark
                cbPartner.setButtonDrawable(viewModel.drawableForChildCheckBox(mark))

                itemClickListener?.invoke(position, mark)
            }
        }
    }

    inner class ViewHolder(val binding: ItemOutletCreateTaskBinding) :
        RecyclerView.ViewHolder(binding.root)
}
