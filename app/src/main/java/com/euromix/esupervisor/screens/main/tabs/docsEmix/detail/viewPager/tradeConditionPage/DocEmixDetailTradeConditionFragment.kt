package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.ROWS
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixDetailTradeConditionFragmentBinding

class DocEmixDetailTradeConditionFragment :
    Fragment(R.layout.doc_emix_detail_trade_condition_fragment) {

    private val binding by viewBinding<DocEmixDetailTradeConditionFragmentBinding>()
    private val adapter = DocEmixDetailTradeConditionAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRowsTradeCondition.adapter = adapter

        arguments?.parcelable<List<RowTradeCondition>>(ROWS)
            ?.let {
                adapter.rowTradeCondition = it
            }
    }

    companion object {
        fun newInstance(rows: List<RowTradeCondition>?): DocEmixDetailTradeConditionFragment =
            DocEmixDetailTradeConditionFragment().also { fragment ->
                rows?.let {
                    fragment.arguments = bundleOf(ROWS to rows)
                }
            }
    }
}