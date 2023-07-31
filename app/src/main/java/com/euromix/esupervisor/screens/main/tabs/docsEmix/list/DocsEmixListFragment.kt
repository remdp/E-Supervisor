package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
//import com.euromix.esupervisor.app.utils.designedPeriodView
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DocsEmixListFragment : BaseFragment(R.layout.doc_emix_list_fragment) {

    override val viewModel by viewModels<DocsEmixListViewModel>()

    private val binding by viewBinding<DocEmixListFragmentBinding>()
    private val args by navArgs<DocsEmixListFragmentArgs>()

    private val adapter = DocsEmixAdapter { docEmix ->
        val direction =
            DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocEmixDetailFragment(
                extId = docEmix.extId,
                titleData = TitleData(
                    docEmix.number,
                    with(docEmix.date) { textDate(this) })
            )
        findNavController().navigate(direction)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers(view)
        setupListeners()
        viewModel.updateSelection(args.selection)

        setPeriodSelection(
            binding.iSelection.etPeriod,
            viewModel.selection.value?.period,
            parentFragmentManager
        ) {
            viewModel.updatePeriod(it)
        }


    }

    private fun setupObservers(view: View) {

        viewModel.viewState
            .flowWithLifecycle(lifecycle)
            .onEach {
                adapter.docsEmix = it.items
            }
            .launchIn(lifecycleScope)

//        viewModel.docsEmix.observeResults(this, view, binding.vResult, binding.srl) {
//
//        }
//
//        viewModel.selection.observe(viewLifecycleOwner) { viewModel.reload() }

    }

    private fun setupListeners() {
       // binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.srl.setOnRefreshListener { viewModel.getDocsEmixNew() }
        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.iSelection.ivFunnel.setOnClickListener {

            val direction =
                DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocsEmixSelectionFragment(
                    selection = viewModel.selection.value
                )
            findNavController().navigate(direction)

        }
    }

}