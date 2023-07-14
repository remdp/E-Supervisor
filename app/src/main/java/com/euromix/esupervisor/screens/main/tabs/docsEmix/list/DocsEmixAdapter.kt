package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.databinding.ItemDocEmixListFragmentBinding

class DocsEmixAdapter(
    private val docEmixActionListener: (docEmix: DocEmix) -> Unit
) :
    RecyclerView.Adapter<DocsEmixAdapter.DocsEmixViewHolder>(), View.OnClickListener {

    var docsEmix: List<DocEmix> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onClick(p0: View?) {
        val docEmix = p0?.tag as DocEmix

        docEmixActionListener.invoke(docEmix)
    }

    override fun getItemCount(): Int {
        return docsEmix.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocsEmixViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDocEmixListFragmentBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        return DocsEmixViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocsEmixViewHolder, position: Int) {

        val docEmix = docsEmix[position]
        with(holder.binding) {
            holder.itemView.tag = docEmix
            Status.designTV(tvStatus, docEmix.status)
            DocEmixOperationType.designTV(tvOperationType,docEmix.operationType, docEmix.status)

            tvDate.text = textDate(docEmix.date)
            tvNumber.text = docEmix.number
            tvPartner.text = docEmix.partner
            tvTradingAgent.text = docEmix.tradingAgent
            tvSum.text = (docEmix.sum ?: "").toString()
        }
    }

    class DocsEmixViewHolder(
        val binding: ItemDocEmixListFragmentBinding
    ) : RecyclerView.ViewHolder(binding.root)

}