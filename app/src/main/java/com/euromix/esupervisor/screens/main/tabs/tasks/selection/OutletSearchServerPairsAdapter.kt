package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.databinding.ItemSelectionBinding

class OutletSearchServerPairsAdapter(
    val viewModel: OutletsSelectionViewModel,
    var itemClickListener: ((position: Int, mark: Boolean) -> Unit)? = null
) : RecyclerView.Adapter<OutletSearchServerPairsAdapter.ViewHolder>() {

    var selectionItems: List<SelectionItem> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = selectionItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val selectionItem = selectionItems[position]

        with(holder.binding.checkBox) {

            text = selectionItem.serverPair.presentation
            setButtonDrawable(viewModel.drawableForChildCheckBox(selectionItem.marked))

            setOnClickListener {

                val mark = !selectionItem.marked
                itemClickListener?.invoke(position, mark)

                setButtonDrawable(viewModel.drawableForChildCheckBox(mark))
            }
        }
    }

    inner class ViewHolder(val binding: ItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)

}