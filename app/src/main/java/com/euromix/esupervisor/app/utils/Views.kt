package com.euromix.esupervisor.app.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.euromix.esupervisor.databinding.DropDownRateItemSpinnerBinding
import com.euromix.esupervisor.databinding.ItemSpinnerBinding

fun getSpinnerView(convertView: View?, parent: ViewGroup, text: String): View {

    val binding = if (convertView == null)
        ItemSpinnerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    else ItemSpinnerBinding.bind(convertView)

    binding.tv.text = text
    return binding.root
}

fun getSpinnerDropDownView(convertView: View?, parent: ViewGroup, text: String): View {

    val binding = if (convertView == null)
        DropDownRateItemSpinnerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    else DropDownRateItemSpinnerBinding.bind(convertView)

    binding.tv.text = text
    return binding.root
}