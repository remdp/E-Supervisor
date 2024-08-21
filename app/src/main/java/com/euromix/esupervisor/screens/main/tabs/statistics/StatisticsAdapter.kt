package com.euromix.esupervisor.screens.main.tabs.statistics

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.getString
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.common.entities.ServerSelectionItem
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticData
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticDetailData
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
import java.util.Base64
import kotlin.math.max

class StatisticsAdapter(
    private val recyclerView: RecyclerView,
    private val onDetailClick: (itemSelection: ServerSelectionItem) -> Unit,
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

        holder.binding.tvCollapse.setOnClickListener {
            currentItem.isExpanded = !currentItem.isExpanded
            renderItem(holder.binding, currentItem)

            if (!currentItem.isExpanded) recyclerView.post {
                recyclerView.layoutManager?.smoothScrollToPosition(
                    recyclerView, RecyclerView.State(), position
                )
            } else {
                currentItem.serverObject.let {
                    onDetailClick(
                        ServerSelectionItem(
                            it.serverPair.id,
                            Base64.getEncoder()
                                .encodeToString(it.serverType.toByteArray(Charsets.UTF_8))
                        )
                    )
                }
            }
        }


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
                tvCollapse.text = root.context.getString(R.string.collapse)
                tvCollapse.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_drop_up_blue, 0
                )
            } else {

                incDetail.root.gone()
                grpTop.visible()
                tvCollapse.text = root.context.getString(R.string.more_details)
                tvCollapse.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_drop_down_blue, 0
                )
            }
        }
    }

    private fun renderItem(
        itemBinding: ItemStatisticBinding, currentItem: VisitsStatisticData
    ) {

        setExpandState(itemBinding, currentItem.isExpanded)

        itemBinding.tvStatDetail.text = currentItem.serverObject.serverPair.presentation
        itemBinding.tvStatDetailChildrenCount.text = currentItem.childrenCount.toString()

        itemBinding.tvStatDetailChildrenCount.visibility(currentItem.childrenCount != 0)
        itemBinding.ivDecipher.visibility(currentItem.childrenCount != 0)

        if (currentItem.isExpanded)
            renderDetailData(currentItem, itemBinding)
        else
            renderCommonData(currentItem, itemBinding)
    }

    private fun renderCommonData(
        currentItem: VisitsStatisticData,
        itemBinding: ItemStatisticBinding
    ) {

        val ctx = itemBinding.root.context
        with(itemBinding) {

            tvPlanVisits.text = planFactString(ctx, currentItem.planVisits)
            tvUnscheduledVisits.text = planFactString(ctx, currentItem.unscheduledVisits)
            tvRegularVisits.text = planFactString(ctx, currentItem.regularVisits)
            tvDistanceVisits.text = planFactString(
                ctx, PlanFact(
                    currentItem.remoteConstantVisits.plan + currentItem.remoteSituationalVisits.plan,
                    currentItem.remoteConstantVisits.fact + currentItem.remoteSituationalVisits.fact
                )
            )
            tvEffectiveVisits.text = planFactString(ctx, currentItem.effectiveVisits)
        }
    }

    private fun renderDetailData(
        currentItem: VisitsStatisticData,
        itemBinding: ItemStatisticBinding
    ) {

        val ctx = itemBinding.root.context
        val detailData = currentItem.detailData

        renderVisitsItem(
            itemBinding.incDetail.iPlanVisits,
            getString(ctx, R.string.planned),
            R.drawable.ic_calendar_statistic,
            detailData?.let { currentItem.planVisits.plan } ?: 0,
            detailData?.let { currentItem.planVisits.fact } ?: 0,
            detailData?.animateCharts ?: false

        )

        renderVisitsItem(
            itemBinding.incDetail.iUnscheduledVisits,
            getString(ctx, R.string.unscheduled),
            R.drawable.ic_calendar_statistic_cross,
            detailData?.let { currentItem.unscheduledVisits.plan } ?: 0,
            detailData?.let { currentItem.unscheduledVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iRegularVisits,
            getString(ctx, R.string.regular_visits),
            R.drawable.ic_run_man_statistic,
            detailData?.let { currentItem.regularVisits.plan } ?: 0,
            detailData?.let { currentItem.regularVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iDistanceVisits,
            getString(ctx, R.string.distance_constants),
            R.drawable.ic_phone_statistic,
            detailData?.let { currentItem.remoteConstantVisits.plan } ?: 0,
            detailData?.let { currentItem.remoteConstantVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iOneTimeVisits,
            getString(ctx, R.string.distance_situational),
            R.drawable.ic_phone_statistic,
            detailData?.let { currentItem.remoteSituationalVisits.plan } ?: 0,
            detailData?.let { currentItem.remoteSituationalVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderVisitsItem(
            itemBinding.incDetail.iEffectiveVisits,
            getString(ctx, R.string.effective_visits),
            R.drawable.ic_stars_statistic,
            detailData?.let { currentItem.effectiveVisits.plan } ?: 0,
            detailData?.let { currentItem.effectiveVisits.fact } ?: 0,
            detailData?.animateCharts ?: false
        )

        renderManufacturersItem(
            itemBinding.incDetail, prepareManufacturersBarData(
                ctx,
                detailData?.manufacturersRoute?.toFloat() ?: 0f,
                detailData?.manufacturersPortfolio?.toFloat() ?: 0f,
                detailData?.manufacturersSale?.toFloat() ?: 0f
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
                tvOverdueReceivablesRoute.text = detailData.overdueReceivablesRoute.toStringOrDefault()
                tvAmountPayments.text = detailData.amountPayments.toStringOrDefault()
                tvOutletsTime.text = currentItem.outletsTime
                tvTravelTime.text = currentItem.travelTime

                tvManufacturersInPortfolioLabel.text = ctx.getString(
                    R.string.the_number_of_manufacturers_in_the_portfolio,
                    detailData.manufacturersPortfolio
                )

            } ?: run {

                tvOutletsTime.text = getString(ctx, R.string.empty_time)
                tvTravelTime.text = getString(ctx, R.string.empty_time)
                tvAvgNumberOfOrders20.text = "0"
                tvAvgNumberOrders.text = "0"
                tvAvgAmountOrders.text = "0"
                tvOverdueReceivables.text = "0"
                tvOverdueReceivablesRoute.text = "0"
                tvAmountPayments.text = "0"

                tvManufacturersInPortfolioLabel.text = ctx.getString(
                    R.string.the_number_of_manufacturers_in_the_portfolio,
                    0
                )

            }

            rvManufacturersLogo.adapter = StatisticsManufacturerAdapter().apply {
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

    private fun planFactString(ctx: Context, planFact: PlanFact) =
        ctx.getString(R.string.plan_fact, planFact.fact, planFact.plan)

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

        val ctx = item.chart.context
        val percent = (if (plan != 0f) (factF / planF) * 100 else 0f)

        item.tvVisits.text = title
        item.tvPercent.text = ctx.getString(R.string.percent, percent.toInt().toString())

        item.tvVisits.setCompoundDrawablesWithIntrinsicBounds(
            icon, 0, 0, 0
        )

        item.tvFact.text = fact.toString()
        item.tvPlan.text = plan.toString()

        renderChart(
            item.chart,
            prepareVisitsBarData(ctx, planF, factF),
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
                setNoDataText(context.getString(R.string.no_data))
                setNoDataTextColor(App.getColor(context, R.color.colorSelectiveYellow))
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
        val manufacturersSale = detailData?.manufacturersSale?.toFloat() ?: 0f
        val maxIndicator = max(max(manufacturersSale, manufacturersPortfolio), manufacturersRoute)

        with(item) {
            tvMnfChartSale.text = manufacturersSale.toIntString()
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

    private fun prepareVisitsBarData(ctx: Context, plan: Float, fact: Float): BarData {

        val progressList = if (plan != 0f && fact != 0f)
            listOf(BarEntry(0f, plan), BarEntry(0.5f, fact))
        else
            listOf()

        val dataSet = BarDataSet(progressList, "progressList")
        dataSet.setColors(
            ContextCompat.getColor(ctx, R.color.blue_20), ContextCompat.getColor(ctx, R.color.blue)
        )

        dataSet.setDrawValues(false)
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter = ValueFormatter(null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
        return barData
    }

    private fun prepareManufacturersBarData(
        ctx: Context,
        manufacturersRoute: Float,
        manufacturersPortfolio: Float,
        manufacturersSale: Float
    ): BarData {
        val progressList = listOf(
            BarEntry(0f, manufacturersRoute),
            BarEntry(0.5f, manufacturersPortfolio),
            BarEntry(1f, manufacturersSale)
        )

        val dataSet = BarDataSet(progressList, "progressList")
        dataSet.setColors(
            ContextCompat.getColor(ctx, R.color.blue_20),
            ContextCompat.getColor(ctx, R.color.blue_60),
            ContextCompat.getColor(ctx, R.color.blue)
        )

        dataSet.setDrawValues(false)
        dataSet.valueTextSize = 12f
//        dataSet.valueFormatter =
//            ValueFormatter(displayedBar = 0.5f)

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