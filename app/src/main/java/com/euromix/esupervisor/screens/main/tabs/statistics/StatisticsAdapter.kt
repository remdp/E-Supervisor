package com.euromix.esupervisor.screens.main.tabs.statistics

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticData
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticDetailData
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.toIntString
import com.euromix.esupervisor.app.utils.toStringOrDefault
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemStatisticBinding
import com.euromix.esupervisor.databinding.ItemStatisticDetailBinding
import com.euromix.esupervisor.databinding.ItemStatisticVisitsBinding
import com.euromix.esupervisor.sources.routes.entities.PlanFact
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import javax.inject.Inject
import kotlin.math.max

class StatisticsAdapter @Inject constructor(
    private val resManager: ResourceManager,
    private val onDetailClick: (item: VisitsStatisticData) -> Unit,
    private val onWatchAllClick: (id: String) -> Unit,
    private val onDecipherClick: (serverObject: ServerObject) -> Unit,
) : ListAdapter<VisitsStatisticData, StatisticsAdapter.ItemViewHolder>(DiffCallBack()) {


    inner class ItemViewHolder(val binding: ItemStatisticBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemStatisticBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val currentItem = getItem(position)
        renderItem(holder.binding, currentItem)

        holder.binding.tvCollapse.setOnClickListener { onDetailClick(currentItem) }

        holder.binding.incDetail.tvWatchAll.setOnClickListener {
            onWatchAllClick(currentItem.serverObject.serverPair.id)
        }

        holder.binding.ivDecipher.setOnClickListener { onDecipherClick(currentItem.serverObject) }

    }

    private fun setExpandState(itemBinding: ItemStatisticBinding, isExpanded: Boolean) {
        with(itemBinding) {

            if (isExpanded) {
                incDetail.root.visible()
                grpTop.gone()
                tvCollapse.text = resManager.getString(R.string.collapse)
                tvCollapse.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_drop_up_blue, 0
                )
            } else {

                incDetail.root.gone()
                grpTop.visible()
                tvCollapse.text = resManager.getString(R.string.more_details)
                tvCollapse.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_drop_down_blue, 0
                )
            }
        }
    }

    private fun renderItem(
        itemBinding: ItemStatisticBinding,
        currentItem: VisitsStatisticData,
        isExpanded: Boolean? = null
    ) {

        val isExpanded = isExpanded ?: currentItem.isExpanded

        setExpandState(itemBinding, isExpanded)

        itemBinding.tvStatDetail.text = currentItem.serverObject.serverPair.presentation
        itemBinding.tvStatDetailChildrenCount.text = currentItem.childrenCount.toString()

        itemBinding.tvStatDetailChildrenCount.visibility(currentItem.childrenCount != 0)
        itemBinding.ivDecipher.visibility(currentItem.childrenCount != 0)

        if (isExpanded)
            renderDetailData(currentItem, itemBinding)
        else
            renderCommonData(currentItem, itemBinding)
    }

    private fun renderCommonData(
        currentItem: VisitsStatisticData,
        itemBinding: ItemStatisticBinding
    ) {

        with(itemBinding) {

            tvPlanVisits.text = planFactString(currentItem.planVisits)
            tvUnscheduledVisits.text = currentItem.unscheduledVisits.fact.toString()
            tvRegularVisits.text = planFactString(currentItem.regularVisits)
            tvDistanceVisits.text = planFactString(
                PlanFact(
                    currentItem.remoteConstantVisits.plan + currentItem.remoteSituationalVisits.plan,
                    currentItem.remoteConstantVisits.fact + currentItem.remoteSituationalVisits.fact
                )
            )
            tvEffectiveVisits.text = planFactString(currentItem.effectiveVisits)
        }
    }

    private fun renderDetailData(
        currentItem: VisitsStatisticData,
        itemBinding: ItemStatisticBinding
    ) {

        val detailData = currentItem.detailData

        renderVisitsItem(
            itemBinding.incDetail.iPlanVisits,
            resManager.getString(R.string.planned),
            R.drawable.ic_calendar_statistic,
            detailData?.let { currentItem.planVisits.plan } ?: 0,
            detailData?.let { currentItem.planVisits.fact } ?: 0,
            detailData?.animateCharts ?: false

        )

        renderVisitsItem(
            itemBinding.incDetail.iRegularVisits,
            resManager.getString(R.string.regular_visits),
            R.drawable.ic_run_man_statistic,
            detailData?.let { currentItem.regularVisits.plan } ?: 0,
            detailData?.let { currentItem.regularVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iDistanceVisits,
            resManager.getString(R.string.distance_constants),
            R.drawable.ic_phone_statistic,
            detailData?.let { currentItem.remoteConstantVisits.plan } ?: 0,
            detailData?.let { currentItem.remoteConstantVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iOneTimeVisits,
            resManager.getString(R.string.distance_situational),
            R.drawable.ic_phone_statistic,
            detailData?.let { currentItem.remoteSituationalVisits.plan } ?: 0,
            detailData?.let { currentItem.remoteSituationalVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iEffectiveVisits,
            resManager.getString(R.string.effective_visits),
            R.drawable.ic_stars_statistic,
            detailData?.let { currentItem.effectiveVisits.plan } ?: 0,
            detailData?.let { currentItem.effectiveVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderManufacturersItem(
            itemBinding.incDetail, prepareManufacturersBarData(
                detailData?.manufacturersRoute?.toFloat() ?: 0f,
                detailData?.manufacturersPortfolio?.toFloat() ?: 0f
            ),
            detailData,
            detailData?.animateCharts ?: false
        )

        with(itemBinding.incDetail) {

            detailData?.let { detailData ->

                tvAvgNumberOfOrders20.text = detailData.avgNumberOrders20.toStringOrDefault()
                tvAvgNumberOrders.text = detailData.avgNumberOrders.toStringOrDefault()
                tvAvgAmountOrders.text = detailData.avgAmountOrders.toStringOrDefault()
                tvOverdueReceivables.text = detailData.overdueReceivables.toStringOrDefault()
                tvOverdueReceivablesRoute.text =
                    detailData.overdueReceivablesRoute.toStringOrDefault()
                tvAmountPayments.text = detailData.amountPayments.toStringOrDefault()
                tvOutletsTime.text = currentItem.outletsTime
                tvManufacturersInPortfolioLabel.text = resManager.getString(
                    R.string.the_number_of_manufacturers_in_the_portfolio,
                    detailData.manufacturersPortfolio
                )
            }

            rvManufacturersLogo.adapter = StatisticsManufacturerAdapter(resManager).apply {
                submitList(
                    if (detailData?.watchAllManufacturersLogo == true)
                        detailData.manufacturersLogo
                    else
                        detailData?.manufacturersLogo?.take(2)
                )
            }

            tvWatchAll.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (detailData?.watchAllManufacturersLogo == true) R.drawable.ic_arrow_drop_up_blue else R.drawable.ic_arrow_drop_down_blue,
                0
            )
        }
    }

    private fun planFactString(planFact: PlanFact) =
        resManager.getString(R.string.plan_fact, planFact.plan, planFact.fact)

    private fun renderVisitsItem(
        item: ItemStatisticVisitsBinding,
        title: String,
        icon: Int,
        plan: Number,
        fact: Number,
        animateChart: Boolean
    ) {

        val planF = plan.toFloat()
        val factF = fact.toFloat()

        val percent = if (planF != 0f) (factF / planF) * 100 else 100f

        item.tvVisits.text = title
        item.tvPercent.text = resManager.getString(R.string.percent, percent.toInt().toString())

        item.tvVisits.setCompoundDrawablesWithIntrinsicBounds(
            icon, 0, 0, 0
        )

        item.tvFact.text = fact.toString()
        item.tvPlan.text = plan.toString()

        renderChart(
            item.chart,
            prepareVisitsBarData(planF, factF),
            max(planF, factF),
            animateChart
        )
        item.chart.post {
            setMargin(item.tvPercent, item.chart.width, percent)
        }
    }


    private fun renderChart(
        chart: HorizontalBarChart,
        barData: BarData,
        maxIndicator: Float,
        animateChart: Boolean
    ) {

        with(chart) {
            setTouchEnabled(false)

            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawAxisLine(false)

            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.isEnabled = false

            axisRight.setDrawGridLines(false)
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
            axisRight.isEnabled = false

            legend.isEnabled = false
            description.isEnabled = false

            data = if (barData.entryCount == 0) {
                setNoDataText(resManager.getString(R.string.no_data))
                setNoDataTextColor(resManager.getColor(R.color.colorSelectiveYellow))
                setNoDataTextTypeface(Typeface.DEFAULT_BOLD)
                null
            } else {
                barData
            }

            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            axisLeft.axisMaximum = maxIndicator
            axisRight.axisMaximum = maxIndicator

            setExtraOffsets(0f, 0f, 0f, 0f)
            minOffset = 0f

            if (animateChart)
                animateY(1000)

            invalidate()
        }
    }

    private fun renderManufacturersItem(
        item: ItemStatisticDetailBinding,
        barData: BarData,
        detailData: VisitsStatisticDetailData?,
        animateChart: Boolean
    ) {

        item.chManufacturers.setTouchEnabled(false)

        val manufacturersRoute = detailData?.manufacturersRoute?.toFloat() ?: 0f
        val manufacturersPortfolio = detailData?.manufacturersPortfolio?.toFloat() ?: 0f
        val maxIndicator = max(manufacturersPortfolio, manufacturersRoute)

        with(item) {
            tvMnfChartPortfolio.text = manufacturersPortfolio.toIntString()
            tvMnfChartRoute.text = manufacturersRoute.toIntString()
        }

        renderChart(
            item.chManufacturers,
            barData,
            maxIndicator,
            animateChart
        )
    }

    private fun prepareVisitsBarData(plan: Float, fact: Float): BarData {

        val dataSet = BarDataSet(listOf(BarEntry(0f, plan), BarEntry(0.5f, fact)), "progressList")
        dataSet.setColors(
            resManager.getColor(R.color.blue_20), resManager.getColor(R.color.blue)
        )

        dataSet.setDrawValues(false)
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter = ValueFormatter(null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        return barData
    }

    private fun prepareManufacturersBarData(
        manufacturersRoute: Float,
        manufacturersPortfolio: Float
    ): BarData {
        val progressList = listOf(
            BarEntry(0.5f, manufacturersRoute),
            BarEntry(1f, manufacturersPortfolio)
        )

        val dataSet = BarDataSet(progressList, "progressList")
        dataSet.setColors(
            resManager.getColor(R.color.blue_20),
            resManager.getColor(R.color.blue)
        )

        dataSet.setDrawValues(false)
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter =
            ValueFormatter()

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        return barData
    }

    private fun setMargin(tv: TextView, chartWidth: Int, percent: Float) {

        val targetPosition = ((chartWidth * percent / 100) - tv.width - percent * 0.5)

        val layoutParams = tv.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.leftMargin = max(0, targetPosition.toInt())
        tv.layoutParams = layoutParams
    }

    class DiffCallBack : DiffUtil.ItemCallback<VisitsStatisticData>() {
        override fun areItemsTheSame(
            oldItem: VisitsStatisticData, newItem: VisitsStatisticData
        ) = oldItem.serverObject.serverPair.id == newItem.serverObject.serverPair.id

        override fun areContentsTheSame(
            oldItem: VisitsStatisticData, newItem: VisitsStatisticData
        ) = oldItem == newItem
    }
}

class ValueFormatter(private val displayedBar: Float? = null) :
    com.github.mikephil.charting.formatter.ValueFormatter() {

    override fun getBarLabel(barEntry: BarEntry?): String {

        return when {
            displayedBar == null -> barEntry?.y?.toInt().toString()
            barEntry?.x == displayedBar -> barEntry.y.toInt().toString()
            else -> ""
        }
    }

    override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
        return value.toInt().toString()
    }
}