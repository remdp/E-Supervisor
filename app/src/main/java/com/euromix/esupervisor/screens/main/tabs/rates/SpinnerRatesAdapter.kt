package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.app.utils.getSpinnerDropDownView
import com.euromix.esupervisor.app.utils.getSpinnerView

class SpinnerRatesAdapter(context: Context, resource: Int, val rates: List<RateStructure>) :
    ArrayAdapter<RateStructure>(context, resource, rates) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerView(convertView, parent, rates[position].rate.presentation)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerDropDownView(
            convertView,
            parent,
            rates[position].rate.presentation)
}