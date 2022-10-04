package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.euromix.esupervisor.R
//import com.euromix.esupervisor.databinding.DocsEmixListBinding
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator

class DocsEmixListFragment : BaseFragment(R.layout.doc_emix_list_fragment) {

    override val viewModel by viewModelCreator { DocsEmixListViewModel() }
    private lateinit var binding: DocEmixListFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DocEmixListFragmentBinding.bind(view)
        binding.rvList.setOnClickListener {
            val docsEmix = it.tag as DocEmix
            val direction =
                DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocEmixDetailFragment(
                    docsEmix.extId
                )
            findNavController().navigate(direction)
        }

        binding.srl.setOnRefreshListener {
            binding.srl.isRefreshing = false
            viewModel.reload()
        }




        binding.vResult.setTryAgainAction { viewModel.reload() }

        val adapter = DocsEmixAdapter { docEmix ->

            val direction =
                DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocEmixDetailFragment(
                    docEmix.extId
                )
            findNavController().navigate(direction)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = layoutManager
        binding.rvList.adapter = adapter

        val divider = DividerItemDecoration(
            binding.rvList.context,
            layoutManager.orientation
        )
        val drawable = binding.rvList.context.getDrawable(R.drawable.line_divider)
        if (drawable != null) {
            divider.setDrawable(drawable)
            binding.rvList.addItemDecoration(divider)
        }

        viewModel.docsEmix.observeResults(this, view, binding.vResult) {
            adapter.docsEmix = it
        }
    }
}