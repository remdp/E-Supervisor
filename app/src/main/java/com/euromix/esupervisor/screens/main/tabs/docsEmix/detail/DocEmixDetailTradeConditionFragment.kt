package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.databinding.DocEmixDetailFragmentBinding
import com.euromix.esupervisor.databinding.DocEmixDetailTradeConditionFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        arguments?.let {
            viewModel.setRowsTradeConditions(requireArguments()[ROWS] as List<RowTradeCondition>)
        }
    }

    companion object {
        fun newInstance(rows: List<RowTradeCondition>): DocEmixDetailTradeConditionFragment {
            val fragment = DocEmixDetailTradeConditionFragment()
            fragment.arguments = bundleOf(ROWS to rows)
            return fragment
        }

        private const val ROWS = "rows"
    }
}