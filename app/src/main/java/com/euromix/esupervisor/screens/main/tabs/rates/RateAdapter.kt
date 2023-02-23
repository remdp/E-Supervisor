package com.euromix.esupervisor.screens.main.tabs.rates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.databinding.ItemDocEmixListFragmentBinding
import com.euromix.esupervisor.databinding.ItemRateFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.list.DocsEmixAdapter

class RateAdapter(private val onItemClicked: (rate: RateData) -> Unit): RecyclerView.Adapter<RateAdapter.ViewHolder>(), View.OnClickListener {

    var rates: List<RateData> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRateFragmentBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rate = rates[position]

        with(holder.binding) {
            holder.itemView.tag = rate
            rate.issueIndicator(this)
        }
    }

    override fun getItemCount(): Int = rates.size

    inner class ViewHolder(val binding: ItemRateFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View?) {

        val rate = v?.tag as RateData

        onItemClicked.invoke(rate)
    }
}