package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.returnRequestPage

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.getDrawable
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.ROWS
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.docEmix.entities.RowReturnRequest
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixDetailReturnRequestFragmentBinding

class DocEmixDetailReturnRequestFragment :
    Fragment(R.layout.doc_emix_detail_return_request_fragment) {

    private val binding by viewBinding<DocEmixDetailReturnRequestFragmentBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DocEmixDetailReturnRequestAdapter()
        binding.rvRowsReturnRequest.adapter = adapter

        val divider = DividerItemDecoration(
            binding.rvRowsReturnRequest.context,
            LinearLayout.VERTICAL
        )
        val drawable = getDrawable(requireContext(), R.drawable.line_divider)
        if (drawable != null) {
            divider.setDrawable(drawable)
            binding.rvRowsReturnRequest.addItemDecoration(divider)
        }

        arguments?.parcelable<List<RowReturnRequest>>(ROWS)
            ?.let {
                println(it)
                adapter.submitList(it)
            }
    }

    companion object {

        fun newInstance(rowsReturnRequest: List<RowReturnRequest>): DocEmixDetailReturnRequestFragment =
            DocEmixDetailReturnRequestFragment().apply {
                arguments = bundleOf(ROWS to rowsReturnRequest)
            }
    }
}