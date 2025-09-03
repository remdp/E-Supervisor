package com.euromix.esupervisor.screens.main.tabs.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.filter.entities.DetailFilterItem
import com.euromix.esupervisor.app.model.filter.entities.FilterItem
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.setIconCollapse
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemSelectionSearchBinding
import com.google.android.material.checkbox.MaterialCheckBox

class FilterItemsAdapter(
    private val filterTitles: Array<String>,
    private val onSearchTextChanged: (position: Int, newText: String) -> Unit,
    private val onDetailItemMarked: (parentPosition: Int, updatedDetailItem: DetailFilterItem) -> Unit,
    private val onAllItemsChecked: (position: Int, isChecked: Boolean) -> Unit
) :
    ListAdapter<FilterItem, FilterItemsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectionSearchBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val filterItem = getItem(position)
        with(holder.binding) {

            val serverPairsAdapter = FilterServerPairsAdapter { updatedDetailItem ->
                onDetailItemMarked.invoke(position, updatedDetailItem)
            }
            rvSelectionItems.adapter = serverPairsAdapter

            val filteredDetailItems = detailItemsWithTextFilter(filterItem.detailFilterItems, filterItem.searchString)
            serverPairsAdapter.submitList(filteredDetailItems)
            setDrawableForParentCheckBox(filteredDetailItems, cbAllCheck)

            if (etSearch.text.toString() != filterItem.searchString) {
                etSearch.setText(filterItem.searchString)
                etSearch.setSelection(etSearch.length())
            }

            etSearch.doAfterTextChanged { editable ->
                onSearchTextChanged.invoke(holder.adapterPosition, editable.toString())
            }

            cbAllCheck.text = filterTitles[position]
            cbAllCheck.setOnCheckedChangeListener { _, _ ->
                val currentMark = isCheckedTopLevel(filterItem.detailFilterItems)
                val newMark = currentMark?.not() ?: true

                onAllItemsChecked.invoke(position, newMark)
            }
        }
    }

    private fun detailItemsWithTextFilter(detailItems: List<DetailFilterItem>, text: String?) =
        if (text?.isNotBlank() == true) {
            detailItems.filter {
                it.serverPair.presentation.contains(text.toString(), ignoreCase = true)
            }
        } else {
            detailItems
        }

    private fun isCheckedTopLevel(detailItems: List<DetailFilterItem>) =
        listOf(true, false).find { flag -> detailItems.all { it.marked == flag } }

    private fun setDrawableForParentCheckBox(
        items: List<DetailFilterItem>,
        vCheckBox: MaterialCheckBox
    ) {
        val drawable = when {
            items.any { it.marked } && items.any { !it.marked } -> R.drawable.ic_checkbox_white_indeterminate
            items.all { it.marked } -> R.drawable.ic_checkbox_white_on
            else -> R.drawable.ic_checkbox_white_off
        }
        vCheckBox.setButtonIconDrawableResource(drawable)
    }

    private fun collapseItems(collapse: Boolean, binding: ItemSelectionSearchBinding) {

        if (collapse) {
            binding.tiSearch.gone()
            binding.rvSelectionItems.gone()
        } else {
            binding.tiSearch.visible()
            binding.rvSelectionItems.visible()
        }

        binding.btnExpand.setIconCollapse(collapse)
    }

    inner class ViewHolder(
        val binding: ItemSelectionSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var collapse = false

        init {
            binding.btnExpand.setOnClickListener {
                collapse = !collapse
                collapseItems(collapse, binding)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FilterItem>() {
        override fun areItemsTheSame(oldItem: FilterItem, newItem: FilterItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FilterItem, newItem: FilterItem) =
            oldItem == newItem
    }
}