package com.euromix.esupervisor.screens.main.tabs.visits.changeType

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.euromix.esupervisor.app.model.visits.entities.ChangeVisitTypeReason
import com.euromix.esupervisor.app.utils.getSpinnerDropDownView
import com.euromix.esupervisor.app.utils.getSpinnerView

class ReasonsAdapter(context: Context, resource: Int, private val reasons: List<ChangeVisitTypeReason>) :
    ArrayAdapter<ChangeVisitTypeReason>(context, resource, reasons) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerView(convertView, parent, reasons[position].reason.presentation)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) =
        getSpinnerDropDownView(
            convertView,
            parent,
            reasons[position].reason.presentation)
}