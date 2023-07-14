package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.databinding.SpinnerDropDownItemBinding

class TaskTypesAdapter(context: Context) :
    ArrayAdapter<ServerPair>(context, R.layout.selection_dropdown_item) {

    var list: List<ServerPair> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return prepareConvertView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return prepareConvertView(position, convertView, parent)
    }

    private fun prepareConvertView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null)
            SpinnerDropDownItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        else SpinnerDropDownItemBinding.bind(convertView)

        binding.ctvItem.text = list[position].presentation

        return binding.root
    }

    override fun getCount() = list.size

}