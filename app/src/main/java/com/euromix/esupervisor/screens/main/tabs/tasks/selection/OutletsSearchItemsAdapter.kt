package com.euromix.esupervisor.screens.main.tabs.tasks.selection


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.size
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setIconCollapse
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemSelectionBinding
import com.euromix.esupervisor.databinding.ItemSelectionSearchBinding

class OutletsSearchItemsAdapter(
    val viewModel: OutletsSelectionViewModel,
    val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<OutletsSearchItemsAdapter.ViewHolder>() {

    var searchItems: List<List<SelectionItem>> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectionSearchBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = searchItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val searchItem = searchItems[position]
        holder.serverPairsAdapter.selectionItems = searchItem

        with(holder.binding) {
            cbOutletTypes.text = nameStringRes(position, holder.binding.root.context)
            cbOutletTypes.setButtonIconDrawableResource(
                viewModel.drawableForParentCheckBox(
                    position
                )
            )

            cbOutletTypes.setOnCheckedChangeListener { _, isChecked ->

                viewModel.changeMark(position, isChecked)

                holder.binding.rvSelectionItems.forEach {
                    val itemBinding = ItemSelectionBinding.bind(it)
                    itemBinding.checkBox.setButtonDrawable(
                        viewModel.drawableForChildCheckBox(
                            isChecked
                        )
                    )
                }
                cbOutletTypes.setButtonIconDrawableResource(
                    viewModel.drawableForParentCheckBox(position)
                )
            }

            holder.serverPairsAdapter.itemClickListener = { downLevelPos: Int, mark: Boolean ->

                viewModel.changeMark(position, mark, downLevelPos)

                cbOutletTypes.setButtonIconDrawableResource(
                    viewModel.drawableForParentCheckBox(position)
                )

            }

            etSearch.doAfterTextChanged { text ->
                text?.let {
                    viewModel.changeText(
                        position,
                        text
                    )
                }
            }

            viewModel.taSelectionEvent.observeEvent(viewLifecycleOwner) {
                if (position == OutletsSelectionViewModel.INDEX_TA_ITEM) updateHolder(
                    holder,
                    it,
                    position
                )
            }

            viewModel.oitSelectionEvent.observeEvent(viewLifecycleOwner) {
                if (position == OutletsSelectionViewModel.INDEX_OIT_ITEM) updateHolder(
                    holder,
                    it,
                    position
                )
            }
        }
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

    private fun updateHolder(holder: ViewHolder, items: List<SelectionItem>, position: Int) {
        holder.serverPairsAdapter.selectionItems = items

        holder.binding.cbOutletTypes.setButtonIconDrawableResource(
            viewModel.drawableForParentCheckBox(position)
        )
    }

    private fun nameStringRes(index: Int, context: Context) = when (index) {
        0 -> App.getString(context, R.string.trading_agents)
        else -> {
            App.getString(context, R.string.outlets_types)
        }
    }

    inner class ViewHolder(
        val binding: ItemSelectionSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var collapse = false
        val serverPairsAdapter = OutletSearchServerPairsAdapter(viewModel)

        init {
            binding.rvSelectionItems.adapter = serverPairsAdapter

            binding.btnExpand.setOnClickListener {
                collapse = !collapse
                collapseItems(collapse, binding)
            }
        }
    }

}