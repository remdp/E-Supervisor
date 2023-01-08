package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.utils.setColorStatus
import com.euromix.esupervisor.app.utils.setNonStandartStatusText
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.databinding.ItemDocEmixListFragmentBinding

typealias DocEmixActionListener = (docEmix: DocEmix) -> Unit

class DocsEmixAdapter(
    private val docEmixActionListener: DocEmixActionListener
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

//        when (p0.id) {
//            R.id.ivMore -> {
//                showPopupMenu(p0)
//            }
//        }
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
            val context = holder.itemView.context

            //tvStatus.text = docEmix.status
            tvStatus.setNonStandartStatusText(docEmix.status)
            tvStatus.setColorStatus(docEmix.status)
//            tvDate.text = context.getString(R.string.from) + " " + docEmix.date.toLocalDate()
//                .toFormatString(context.getString(R.string.day_month_year_date_format))
            tvDate.text = with(docEmix.date) { textDate(this, context) }
            tvNumber.text = docEmix.number
            tvPartner.text = docEmix.partner
            tvOperationType.text = docEmix.operationType
            tvTradingAgent.text = docEmix.tradingAgent
            tvSum.text = (docEmix.sum ?: "").toString()
           // tvSum.text = if (docEmix.sum == null) "" else docEmix.sum.toString()

        }
    }

//    private fun showPopupMenu(v: View) {
//        val popupMenu = PopupMenu(v.context, v)
//        val docEmix = v.tag as DocEmix
//        popupMenu.menu.add(0, 0, Menu.NONE, "Delete")
//        popupMenu.setOnMenuItemClickListener {
//            when (it.itemId) {
//                0 -> {
//                    //     docEmixActionListener.onDocEmixDelete(docEmix)
//                }
//            }
//            return@setOnMenuItemClickListener true
//        }
//
//        popupMenu.show()
//    }

    class DocsEmixViewHolder(
        val binding: ItemDocEmixListFragmentBinding
    ) : RecyclerView.ViewHolder(binding.root)

}