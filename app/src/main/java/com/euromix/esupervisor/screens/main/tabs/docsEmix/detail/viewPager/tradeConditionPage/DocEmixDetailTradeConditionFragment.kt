package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.databinding.DocEmixDetailTradeConditionFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.DocEmixDetailAdapter
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage.DocEmixDetailNewOutletFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage.ViewState
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newPartnerPage.DocEmixDetailNewPartnerFragment
import dagger.hilt.android.AndroidEntryPoint

class DocEmixDetailTradeConditionFragment() :
    BaseFragment(R.layout.doc_emix_detail_trade_condition_fragment) {

    override val viewModel by viewModels<DocEmixDetailTradeConditionViewModel>()

    private lateinit var binding: DocEmixDetailTradeConditionFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DocEmixDetailTradeConditionFragmentBinding.bind(view)
        val adapter = DocEmixDetailAdapter()
        binding.rvRowsTradeCondition.adapter = adapter

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvRowsTradeCondition.layoutManager = layoutManager

        val divider = DividerItemDecoration(
            binding.rvRowsTradeCondition.context,
            layoutManager.orientation
        )
        val drawable = binding.rvRowsTradeCondition.context.getDrawable(R.drawable.line_divider)
        if (drawable != null) {
            divider.setDrawable(drawable)
            binding.rvRowsTradeCondition.addItemDecoration(divider)
        }

        viewModel.rowsTradeConditions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        arguments?.parcelable<List<RowTradeCondition>>(ROWS)
            ?.let { viewModel.setRowsTradeConditions(it) }
    }

    companion object {
        fun newInstance(rows: List<RowTradeCondition>?): DocEmixDetailTradeConditionFragment =
            DocEmixDetailTradeConditionFragment().also { fragment ->
                rows?.let {
                    fragment.arguments = bundleOf(ROWS to rows)
                }
            }

        private const val ROWS = "rows"
    }
}