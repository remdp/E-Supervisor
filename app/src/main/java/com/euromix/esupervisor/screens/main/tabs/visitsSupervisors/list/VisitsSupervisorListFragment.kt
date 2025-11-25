package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.FilterSource
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setDateSelection
import com.euromix.esupervisor.app.utils.setIcon
import com.euromix.esupervisor.app.utils.toLocalDate
import com.euromix.esupervisor.app.utils.toLong
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.VisitsSupervisorsListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.filter.FilterFragmentArgs
import com.euromix.esupervisor.screens.main.tabs.filter.SharedFilterViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class VisitsSupervisorListFragment : BaseFragment(R.layout.visits_supervisors_list_fragment) {

    private val navController: NavController by lazy { findNavController() }
    override val viewModel by viewModels<VisitsSupervisorListViewModel>()
    private val sharedViewModel: SharedFilterViewModel by navGraphViewModels(R.id.visits_supervisors_graph)

    private val binding by viewBinding<VisitsSupervisorsListFragmentBinding>()

    private lateinit var adapter: VisitsSupervisorAdapter

    @Inject
    lateinit var resManager: ResourceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VisitsSupervisorAdapter(viewModel::changeMark) {
            navController.navigate(it)
        }
        binding.rvList.adapter = adapter

        setDateSelection(
            binding.tvDate,
            parentFragmentManager,
            showClearView = false,
            underlineIfNull = false,
            currentDateProvider = { viewModel.viewState.value.selection.period?.first }
        ) {
            it?.let { viewModel.changePeriod(it) }
        }


        attachDatePickerOnClick(
            binding.btnRepeat,
            parentFragmentManager
        ) {
            viewModel.createRepeatStoreChecks(it)
        }

        attachDatePickerOnClick(
            binding.btnTransfer,
            parentFragmentManager
        ) {
            viewModel.transferStoreChecks(it)
        }

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        with(binding) {
            ivFunnel.setOnClickListener {
                navController.navigate(
                    R.id.action_visitsSupervisorsListFragment_to_filter_graph, FilterFragmentArgs(
                        filterSource = FilterSource.FROM_VISITS_SUPERVISORS_FRAGMENT,
                        filterTitles = arrayOf(getString(R.string.supervisors)),
                        flagsTitles = arrayOf(getString(R.string.only_my_visits)),
                        date = viewModel.viewState.value.selection.period?.second?.toLong() ?: 0L,
                        initialSelection = sharedViewModel.getFilterSelection(),
                        graphId = R.id.visits_supervisors_graph
                    ).toBundle()
                )

            }
            srl.setOnRefreshListener { viewModel.reload() }
            vResult.setTryAgainAction { viewModel.reload() }

            binding.tvAllVisits.setOnClickListener { viewModel.setupQuickFilter(VisitFilter.ALL) }
            binding.tvCompletedVisits.setOnClickListener { viewModel.setupQuickFilter(VisitFilter.COMPLETED) }
            binding.tvUnCompletedVisits.setOnClickListener { viewModel.setupQuickFilter(VisitFilter.UNCOMPLETED) }

            etSearch.doAfterTextChanged { text -> viewModel.changeSearchString(text.toString()) }

            tiSearch.setEndIconOnClickListener { etSearch.setText("") }

            binding.ivChecker.setOnClickListener { viewModel.changeShowMarks() }
            binding.cbMarks.setOnClickListener { viewModel.changeMarks() }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    renderState(state)

                    if (state.scrollToTop) {
                        binding.rvList.post { binding.rvList.scrollToPosition(0) }
                        viewModel.onScrolledToTop()
                    }
                }
            }
        }

        viewModel.repeatStoreCheckCreationResult.observeEvent(viewLifecycleOwner) { result ->
            handleProcessingResult(
                result = result,
                titleString = R.string.repeat_store_checks_creation_result_title,
                successString = R.string.repeat_store_checks_creation_result,
                errorString = R.string.repeat_store_checks_creation_error
            )
        }

        viewModel.transferStoreCheckResult.observeEvent(viewLifecycleOwner) { result ->
            handleProcessingResult(
                result = result,
                titleString = R.string.store_checks_transferring_result_title,
                successString = R.string.transferring_store_checks_result,
                errorString = R.string.store_checks_transferring_error
            )
        }
    }

    private fun handleProcessingResult(
        result: Result<*>,
        @StringRes titleString: Int,
        @StringRes successString: Int,
        @StringRes errorString: Int
    ) {
        when (result) {
            is Success -> {
                hideActionLoading()
                showResultProcessingStoreChecks(
                    message = getString(successString, result.value),
                    title = titleString
                )
            }
            is Error -> {
                hideActionLoading()
                showResultProcessingStoreChecks(
                    message = getString(errorString, result.error.message),
                    title = titleString
                )
            }
            else -> {
                showActionLoading()
            }
        }
    }

    private fun renderState(state: VisitsSupervisorListViewModel.ViewState) {
        designByViewState(
            state as BaseViewState,
            binding.root,
            binding.vResult,
            binding.srl
        )

        with(binding) {
            tvDate.text = state.selection.period?.first?.toText()

            if (!state.isLoading && state.error == null) {
                adapter.submitList(state.displayVisits)

                val currentTotalMark = state.totalMark
                cbMarks.setIcon(currentTotalMark)
                if (state.showMarks) cbMarks.visible()
                else cbMarks.gone()

                when (state.quickFilter) {
                    VisitFilter.ALL -> setTextColorAndBackground(binding.tvAllVisits)
                    VisitFilter.COMPLETED -> setTextColorAndBackground(binding.tvCompletedVisits)
                    VisitFilter.UNCOMPLETED -> setTextColorAndBackground(binding.tvUnCompletedVisits)
                }

                if (state.totalMark != false) {
                    clAppbarBottom.visible()

                    when (state.quickFilter) {
                        VisitFilter.ALL -> {
                            btnRepeat.visible()
                            btnTransfer.visible()
                        }
                        VisitFilter.COMPLETED -> {
                            btnRepeat.visible()
                            btnTransfer.gone()
                        }
                        VisitFilter.UNCOMPLETED -> {
                            btnRepeat.gone()
                            btnTransfer.visible()
                        }
                    }
                } else {
                    clAppbarBottom.gone()
                }
            }
        }
    }

    private fun setTextColorAndBackground(pressedTV: TextView) {
        with(binding) {
            listOf(
                tvAllVisits, tvCompletedVisits, tvUnCompletedVisits
            ).forEach {
                if (it == pressedTV) {
                    it.setTextColor(resManager.getColor(R.color.blue))
                    it.background =
                        resManager.getDrawable(R.drawable.bg_4dp_border_blue)
                } else {
                    it.setTextColor(resManager.getColor(R.color.gray_400))
                    it.background = resManager.getDrawable(R.drawable.bg_4dp_gray_100_gray_border)
                }
            }
        }
    }

    private fun showActionLoading() {
        binding.vResult.setResult(this, Pending<Unit>(), true)
    }

    private fun hideActionLoading() {
        binding.vResult.setResult(this, Success(""), false)
    }

    private fun showResultProcessingStoreChecks(
        message: String,
        @StringRes title: Int
    ) {

        val builder = AlertDialog.Builder(requireContext())

        val dialog = with(builder) {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
        }
        dialog.show()
    }

    fun attachDatePickerOnClick(
        view: View,
        fm: FragmentManager,
        dateUpdater: ((date: LocalDate) -> Unit)
    ) {
        view.setOnClickListener {

            val currentDate = LocalDate.now()

            val picker = MaterialDatePicker.Builder.datePicker().apply {
                setSelection(currentDate?.toLong())
            }.build()

            picker.addOnPositiveButtonClickListener { dateLong ->
                dateUpdater.invoke(dateLong.toLocalDate())
            }
            picker.show(fm, picker.toString())
        }
    }
}