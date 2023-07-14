package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.databinding.SpinnerDropDownItemBinding

class SpinnerRatesAdapter(context: Context, resource: Int, val rates: List<Rate>) :
    ArrayAdapter<Rate>(context, resource, rates) {

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

        binding.ctvItem.text = context.getString(rates[position].nameStringsRes())

        return binding.root
    }
}