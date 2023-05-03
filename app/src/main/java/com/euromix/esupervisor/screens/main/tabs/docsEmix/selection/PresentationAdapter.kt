package com.euromix.esupervisor.screens.main.tabs.docsEmix.selection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.euromix.esupervisor.app.model.docsEmix.entities.ItemSelection
import com.euromix.esupervisor.databinding.SelectionDropdownItemBinding

class PresentationAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val list: List<ItemSelection>
) : ArrayAdapter<ItemSelection>(context, layoutResource, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding = if (convertView == null)
            SelectionDropdownItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false
        )
        else SelectionDropdownItemBinding.bind(convertView)

        binding.tvPresentation.text = list[position].presentation
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }
}