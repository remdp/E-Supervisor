package com.euromix.esupervisor.screens.main.tabs.visits.changeType

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.visits.entities.ChangeVisitTypeReason
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.showErrors
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ChangeVisitTypeDialogBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.visits.list.VisitsListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeVisitTypeDialog(private val ids: List<String>, private val visitType: VisitType) :
    DialogFragment() {

    //todo try replace by viewBinding
    private lateinit var binding: ChangeVisitTypeDialogBinding

    val viewModel by viewModels<ChangeVisitTypeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = ChangeVisitTypeDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {

        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.btnOk.setOnClickListener {
            viewModel.viewState.currentReason?.reason?.id?.let { reasonId ->

                if (viewModel.viewState.currentReason?.requiredDescription == true && viewModel.viewState.comment.isEmpty())
                    showErrors(requireContext(), listOf(R.string.description))
                else
                    viewModel.changeVisitsType(
                        visitType,
                        ids,
                        reasonId,
                        binding.etReason.text.toString()
                    )
            }
        }

        binding.etReason.addTextChangedListener {
            viewModel.changeReasonText(it.toString())
            renderState()
        }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {

            designByViewState(
                viewModel.viewState as BaseViewState,
                binding.root,
                binding.vResult
            )

            renderState()
        }

        viewModel.changeVisitTypeEvent.observeEvent(viewLifecycleOwner) { changingResult ->
            showVisitsChangeDialog(changingResult)
        }
    }

    private fun showVisitsChangeDialog(changingResult: String) {

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.result_changing_visits_title)
            .setMessage(getString(R.string.result_changing_visits, changingResult))
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()

        dismiss()

        setFragmentResult(
            VisitsListFragment.NEED_UPDATE,
            Bundle()
        )
    }

    private fun renderState() {

        if (!viewModel.viewState.isLoading && viewModel.viewState.error == null)
            setupSpinner(viewModel.viewState.reasons)

        binding.tvVisitsCount.text = getString(
            R.string.visits_count, ids.size.toString()
        )

        if (viewModel.viewState.currentReason?.requiredDescription == true)
            binding.etReason.visible()
        else
            binding.etReason.gone()

        if (viewModel.viewState.comment.isEmpty())
            binding.etReason.setBackgroundResource(R.drawable.bg_underline_red)
        else binding.etReason.setBackgroundResource(
            R.drawable.bg_8dp_white_border_gray_200
        )
    }

    private fun setupSpinner(reasons: List<ChangeVisitTypeReason>) {

        if (binding.spReasons.adapter == null) {
            binding.spReasons.adapter = ReasonsAdapter(
                requireContext(), R.layout.item_spinner, reasons
            )
        }

        binding.spReasons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                val currentReason = parent?.getItemAtPosition(position) as ChangeVisitTypeReason

                viewModel.changeReason(currentReason)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    companion object {
        fun newInstance(
            ids: List<String>,
            visitType: VisitType
        ) = ChangeVisitTypeDialog(ids, visitType)

    }
}