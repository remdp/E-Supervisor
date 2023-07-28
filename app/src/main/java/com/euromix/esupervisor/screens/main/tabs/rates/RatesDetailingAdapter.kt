package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.App.Companion.getString
import com.euromix.esupervisor.app.enums.RatesDetailing
import com.euromix.esupervisor.app.utils.getSpinnerDropDownView
import com.euromix.esupervisor.app.utils.getSpinnerView

class RatesDetailingAdapter(
    context: Context,
    resource: Int,
    private val ratesDetailing: List<RatesDetailing>
) :
    ArrayAdapter<RatesDetailing>(context, resource, ratesDetailing) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerView(
            convertView,
            parent,
            getString(parent.context, ratesDetailing[position].nameStringRes())
        )


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerDropDownView(
            convertView,
            parent,
            getString(parent.context, ratesDetailing[position].nameStringRes())
        )
}