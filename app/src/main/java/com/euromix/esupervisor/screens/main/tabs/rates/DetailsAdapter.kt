package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.databinding.DropDownRateItemDetailBinding

class DetailsAdapter(
    context: Context, resource: Int, private val detailsRate: List<String>
) : ArrayAdapter<String>(context, resource, detailsRate) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            DropDownRateItemDetailBinding.inflate(LayoutInflater.from(context), parent, false)
        } else DropDownRateItemDetailBinding.bind(convertView)

        binding.tv.text =  detailsRate[position]

        return binding.root
    }
}