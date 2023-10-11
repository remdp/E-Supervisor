package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.app.utils.getSpinnerDropDownView
import com.euromix.esupervisor.app.utils.getSpinnerView

class RatesDetailingAdapter(
    context: Context,
    resource: Int,
    private val ratesDetailing: List<String>
) :
    ArrayAdapter<String>(context, resource, ratesDetailing) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerView(
            convertView,
            parent,
            ratesDetailing[position]
        )

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerDropDownView(
            convertView,
            parent,
            ratesDetailing[position]
        )
}