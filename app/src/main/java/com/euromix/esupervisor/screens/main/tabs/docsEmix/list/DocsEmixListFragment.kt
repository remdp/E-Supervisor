package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.SELECTION_KEY
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocsEmixListFragment : BaseFragment(R.layout.doc_emix_list_fragment) {

    override val viewModel by viewModels<DocsEmixListViewModel>()
    private val binding by viewBinding<DocEmixListFragmentBinding>()

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

        setupObservers()
        setupListeners()

        setPeriodSelection(
            binding.iSelection.etPeriod,
            viewModel.viewState.value?.period,
            parentFragmentManager
        ) {
            viewModel.updatePeriod(it)
        }
    }

    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            viewModel.afterUpdateState(adapter, binding, this as BaseFragment)
        }

        setFragmentResultListener(SELECTION_KEY) { requestKey, bundle ->

            val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(requestKey, DocsEmixSelection::class.java)
            } else {
                bundle.getParcelable(requestKey)
            }

            if (selection is DocsEmixSelection) viewModel.updateSelection(selection)

        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.iSelection.ivFunnel.setOnClickListener {
            val direction =
                DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocsEmixSelectionFragment(
                    selection = viewModel.viewState.value?.selection
                )
            findNavController().navigate(direction)
        }
    }
}