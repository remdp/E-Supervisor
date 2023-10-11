package com.euromix.esupervisor.app.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.databinding.SelectionDropdownItemBinding
import com.euromix.esupervisor.screens.main.tabs.rates.DetailsAdapter

fun popupWindow(
    anchor: View,
    list: List<String>,
    onItemCLick: (dimension: String) -> Unit
): PopupWindow {

    val context = anchor.context

    val popupWindow = PopupWindow()

    val adapter = DetailsAdapter(context, R.layout.drop_down_rate_item_detail, list)

    val listView = ListView(context)
    listView.adapter = adapter


    popupWindow.isFocusable = true
    popupWindow.width = anchor.width
    popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
    popupWindow.setBackgroundDrawable(
        App.getDrawable(
            context,
            R.drawable.bg_8dp_white_border
        )
    )

    listView.onItemClickListener =
        AdapterView.OnItemClickListener { parent, view, position, id ->
            onItemCLick(list[position])
            popupWindow.dismiss()
        }

    popupWindow.contentView = listView
    return popupWindow

}

fun popupWindowForSelections(
    context: Context,
    list: List<ServerPair>,
    onItemClickListener: (ServerPair) -> Unit
): PopupWindow {

    val popupWindow = PopupWindow()

    val adapter: ArrayAdapter<ServerPair> = PresentationAdapter(
        context, R.layout.selection_dropdown_item, list
    )

    val listView = ListView(context)
    listView.adapter = adapter

    // set on item selected
    listView.onItemClickListener =
        AdapterView.OnItemClickListener { parent, view, position, id ->
            onItemClickListener.invoke(list[position])
            popupWindow.dismiss()
        }

    // some other visual settings for popup window
    popupWindow.isFocusable = true
    popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
    popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
    popupWindow.setBackgroundDrawable(
        App.getDrawable(
            context,
            R.drawable.bg_8dp_white
        )
    )

    popupWindow.contentView = listView
    return popupWindow
}

class PresentationAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val list: List<ServerPair>
) : ArrayAdapter<ServerPair>(context, layoutResource, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val binding = if (convertView == null)
            SelectionDropdownItemBinding.inflate(
                LayoutInflater.from(parent.context),
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