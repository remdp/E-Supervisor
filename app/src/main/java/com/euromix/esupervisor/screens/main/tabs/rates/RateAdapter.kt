package com.euromix.esupervisor.screens.main.tabs.rates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.rates.entities.RateDataRow
import com.euromix.esupervisor.app.utils.clear
import com.euromix.esupervisor.app.utils.customIndicator.CustomProgressIndicator
import com.euromix.esupervisor.databinding.ItemRateFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class RateAdapter(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onItemClicked: (v: View?) -> Unit
) :
    RecyclerView.Adapter<RateAdapter.ViewHolder>(), View.OnClickListener {

    var rates: List<RateDataRow> = emptyList()
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

            tvTradingAgent.text = rate.rateObject
            tvPlan.text = DecimalFormat("###,###.##").format(rate.plan)
            tvFact.text = DecimalFormat("###,###.##").format(rate.fact)

            val plan = rate.plan
            val fact = rate.fact

            if (plan != 0f && fact != 0f) {

                lifecycleScope.launch {
                    delay(200)

                    val colorPi = getColor(
                        root.context,
                        if (fact < plan) R.color.blue else R.color.green
                    )

                    val percentage = fact / plan
                    var startAngle = (270 + (percentage - percentage.toInt()) * 360).toInt()
                    if (startAngle > 360) startAngle -= 360

                    pi.drawCircularProgress(
                        true,
                        true,
                        colorPi,
                        8,
                        startAngle,
                        100,
                        100,
                        CustomProgressIndicator.ProgressTextAdapter {
                            return@ProgressTextAdapter (percentage * 100).toInt().toString() + "%"
                        })
                }
            } else {
                pi.clear(8)
            }
        }
    }

    override fun getItemCount(): Int = rates.size

    inner class ViewHolder(val binding: ItemRateFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View?) {
        onItemClicked.invoke(v)
    }
}