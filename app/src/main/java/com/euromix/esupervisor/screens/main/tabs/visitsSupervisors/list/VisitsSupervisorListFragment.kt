package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.FilterSource
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setDateSelection
import com.euromix.esupervisor.app.utils.toLong
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.VisitsSupervisorsListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.filter.FilterFragmentArgs
import com.euromix.esupervisor.screens.main.tabs.filter.SharedFilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitsSupervisorListFragment : BaseFragment(R.layout.visits_supervisors_list_fragment) {

    private val navController: NavController by lazy { findNavController() }
    override val viewModel by viewModels<VisitsSupervisorListViewModel>()
    private val sharedViewModel: SharedFilterViewModel by navGraphViewModels(R.id.visits_supervisors_graph)

    private val binding by viewBinding<VisitsSupervisorsListFragmentBinding>()

    private lateinit var adapter: VisitsSupervisorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VisitsSupervisorAdapter(viewModel::changeMark) {
            navController.navigate(it)
        }
        binding.rvList.adapter = adapter

        renderState()

        setDateSelection(
            binding.tvDate,
            parentFragmentManager,
            showClearView = false,
            underlineIfNull = false,
            currentDateProvider = { viewModel.viewState.selection.period?.first }
        ) {
            it?.let { viewModel.changePeriod(it) }
        }

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        with(binding) {
            ivFunnel.setOnClickListener {
                navController.navigate(R.id.action_visitsSupervisorsListFragment_to_filter_graph,  FilterFragmentArgs(
                    filterSource = FilterSource.FROM_VISITS_SUPERVISORS_FRAGMENT,
                    filterTitles = arrayOf(getString(R.string.supervisors)),
                    flagsTitles = arrayOf(getString(R.string.only_my_visits)),
                    date = viewModel.viewState.selection.period?.second?.toLong() ?: 0L,
                    initialSelection = sharedViewModel.getFilterSelection(),
                    graphId = R.id.visits_supervisors_graph
                ).toBundle())

            }
            srl.setOnRefreshListener { viewModel.reload() }
            vResult.setTryAgainAction { viewModel.reload() }

            etSearch.doAfterTextChanged { text -> viewModel.changeSearchString(text.toString()) }

            tiSearch.setEndIconOnClickListener { etSearch.setText("") }
        }
    }

    private fun setupObservers() {

        sharedViewModel.filterResult.observe(viewLifecycleOwner) { filter ->
            viewModel.changeSelection(
                onlyMyVisits = filter.flags[0].flag,
                selection = filter.items[0].detailFilterItems.filter { it.marked }
                    .map { it.serverPair.id }
            )
        }

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }

        viewModel.repeatStoreCheckCreationResult.observeEvent(viewLifecycleOwner) {
            if (it is Success || it is Error) {
                showResultRepeatStoreChecksCreate(it)
                binding.vResult.setResult(this, it, false)
            } else binding.vResult.setResult(this, it, true)
        }
    }

    private fun renderState() {
        val viewState = viewModel.viewState
        designByViewState(
            viewState as BaseViewState,
            binding.root,
            binding.vResult,
            binding.srl
        )

        with(binding) {
            tvDate.text = viewState.selection.period?.first?.toText()

            if (!viewState.isLoading && viewState.error == null) {
                adapter.submitList(viewModel.getListForSubmit())
            }
        }
    }

    private fun showResultRepeatStoreChecksCreate(result: Result<String>) {

        val message = if (result is Success) getString(
            R.string.repeat_store_checks_creation_result, result.value
        ) else getString(R.string.repeat_store_checks_creation_error)

        val builder = AlertDialog.Builder(requireContext())

        val dialog = with(builder) {
            setTitle(R.string.repeat_store_checks_creation_result_title)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
        }
        dialog.show()
    }
}