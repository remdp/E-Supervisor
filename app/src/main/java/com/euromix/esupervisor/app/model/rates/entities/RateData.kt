package com.euromix.esupervisor.app.model.rates.entities

import android.os.Parcelable
import com.euromix.esupervisor.databinding.ItemRateFragmentBinding
import kotlinx.parcelize.Parcelize

@Parcelize
data class RateData(
    val rateObject: String,
    val tradingAgentId: String? = null,
    val plan: Float,
    val fact: Float
):Parcelable {
    fun issueIndicator(
        itemIndicator: ItemRateFragmentBinding,
    ) {
        with(itemIndicator) {
            tvTradingAgent.text = rateObject
            tvPlan.text = plan.toInt().toString()
            tvFact.text = fact.toInt().toString()

            var percentText = ""
            if (plan.toInt() != 0) {
                percentText = (fact.toInt() * 100 / plan.toInt()).toString() + " %"
                pb.progress =
                    (fact.toInt() * 100 / plan.toInt())
            }

            tvPercent.text = percentText

        }
    }
}
