package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.databinding.SpinnerRatesDropDownItemBinding

class SpinnerRatesAdapter(context: Context, resource: Int, val rates: List<Rate>) :
    ArrayAdapter<Rate>(context, resource, rates) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = SpinnerRatesDropDownItemBinding.inflate(inflater, parent, false)

        binding.ctvItem.text = context.getString(rates[position].nameStringsRes())

        return binding.root

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val binding = SpinnerRatesDropDownItemBinding.inflate(inflater, parent, false)

        binding.ctvItem.text = context.getString(rates[position].nameStringsRes())

        return binding.root

    }
}