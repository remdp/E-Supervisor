package com.euromix.esupervisor.screens.main.tabs.visits.list

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.visits.entities.VisitsListSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setIcon
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.VisitsListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.visits.changeType.ChangeVisitTypeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitsListFragment : BaseFragment(R.layout.visits_list_fragment) {

    override val viewModel by viewModels<VisitsListViewModel>()

    private val binding by viewBinding<VisitsListFragmentBinding>()

    private lateinit var adapter: VisitsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VisitsAdapter(requireContext(), viewModel::changeMark) { extId, visitType ->
            ChangeVisitTypeDialog.newInstance(listOf(extId), visitType)
                .show(parentFragmentManager, null)
        }
        binding.rvList.adapter = adapter

        setPeriodSelection(
            binding.iSelection.etPeriod, viewModel.selection.period, parentFragmentManager
        ) {
            viewModel.changePeriod(it)
        }

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.tvAllVisits.setOnClickListener { viewModel.setupQuickFilter() }
        binding.tvRegularVisits.setOnClickListener { viewModel.setupQuickFilter(VisitType.REGULAR) }
        binding.tvRemoteVisits.setOnClickListener { viewModel.setupQuickFilter(VisitType.REMOTE) }
        binding.tvOneTimeVisits.setOnClickListener { viewModel.setupQuickFilter(VisitType.ONETIME) }

        binding.etSearch.doAfterTextChanged { text -> viewModel.changeSearchString(text.toString()) }

        binding.tiSearch.setEndIconOnClickListener { binding.etSearch.setText("") }

        binding.ivChecker.setOnClickListener { viewModel.changeShowMarks() }

        binding.cbMarks.setOnClickListener { viewModel.changeMarks() }

        binding.iSelection.ivFunnel.setOnClickListener {
            val direction =
                VisitsListFragmentDirections.actionVisitsListFragmentToVisitsListSelectionFragment(
                    selection = viewModel.selection
                )
            findNavController().navigate(direction)
        }

        binding.btnRegular.setOnClickListener {
            ChangeVisitTypeDialog.newInstance(viewModel.markedVisits(), VisitType.REGULAR)
                .show(parentFragmentManager, null)
        }
        binding.btnRemote.setOnClickListener {
            ChangeVisitTypeDialog.newInstance(viewModel.markedVisits(), VisitType.REMOTE)
                .show(parentFragmentManager, null)
        }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.selectionEvent.observeEvent(viewLifecycleOwner) {
            viewModel.reload()
        }

        setFragmentResultListener(Const.SELECTION_KEY) { requestKey, bundle ->

            val selection: VisitsListSelection?
            val cancelSelection: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selection = bundle.getParcelable(requestKey, VisitsListSelection::class.java)
                cancelSelection = bundle.getBoolean(Const.CANCEL)
            } else {
                selection = bundle.getParcelable(requestKey)
                cancelSelection = bundle.getBoolean(Const.CANCEL)
            }

            if (!cancelSelection)
                viewModel.updateSelection(selection)
        }

        setFragmentResultListener(NEED_UPDATE) { _, _ ->
            viewModel.reload()
        }
    }


    private fun renderState(viewState: VisitsListViewModel.ViewState) {
        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult, binding.srl
        )

        if (!viewState.isLoading && viewState.error == null) adapter.submitList(viewModel.getListForSubmit())

        val visitsData = viewModel.visitsData()

        with(binding) {

            val currentTotalMark = viewState.totalMark
            cbMarks.setIcon(currentTotalMark)
            if (viewState.showMarks) cbMarks.visible()
            else cbMarks.gone()

            tvAllVisits.text = getString(
                R.string.visits_count, visitsData[VisitsListViewModel.TOTAL_VISITS].toString()
            )
            tvRegularVisits.text = getString(
                R.string.visits_counts,
                visitsData[VisitsListViewModel.REGULAR_VISITS_DONE],
                visitsData[VisitsListViewModel.REGULAR_VISITS]
            )

            tvRemoteVisits.text = getString(
                R.string.visits_counts,
                visitsData[VisitsListViewModel.REMOTE_VISITS_DONE],
                visitsData[VisitsListViewModel.REMOTE_VISITS]
            )

            tvOneTimeVisits.text = getString(
                R.string.visits_counts,
                visitsData[VisitsListViewModel.ONE_TIME_VISITS_DONE],
                visitsData[VisitsListViewModel.ONE_TIME_VISITS]
            )

            when (viewState.quickFilter) {
                VisitType.REGULAR -> setTextColorAndBackground(binding.tvRegularVisits)
                VisitType.REMOTE -> setTextColorAndBackground(binding.tvRemoteVisits)
                VisitType.ONETIME -> setTextColorAndBackground(binding.tvOneTimeVisits)
                else -> setTextColorAndBackground(binding.tvAllVisits)
            }

            if (viewModel.scrollToTop()) rvList.post { rvList.scrollToPosition(0) }

            if (viewState.totalMark != false) clAppbarBottom.visible()
            else clAppbarBottom.gone()
        }
    }

    private fun setTextColorAndBackground(pressedTV: TextView) {
        val context = requireContext()

        with(binding) {
            listOf(
                tvAllVisits, tvRegularVisits, tvRemoteVisits, tvOneTimeVisits
            ).forEach {
                if (it == pressedTV) {
                    it.setTextColor(App.getColor(context, R.color.blue))
                    it.background =
                        App.getDrawable(context, R.drawable.bg_4dp_light_alpha_60_border_blue)
                } else {
                    it.setTextColor(App.getColor(context, R.color.gray_400))
                    it.background = App.getDrawable(
                        context, R.drawable.bg_4dp_light_alpha_60_border_dark_alpha_10
                    )
                }
            }
        }
    }

    companion object {
        const val NEED_UPDATE = "NEED_UPDATE"
    }
}

