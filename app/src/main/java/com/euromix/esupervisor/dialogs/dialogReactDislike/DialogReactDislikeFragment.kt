package com.euromix.esupervisor.dialogs.dialogReactDislike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.dateToString
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.designedDateView
import com.euromix.esupervisor.app.utils.dialogErrors
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setDateSelection
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.DialogReasonRejectionCustomBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class DialogReactDislikeFragment(
    abilityCreateTask: Boolean,
    id: String?,
    private val taskCreator: (reason: String, createDislikeTask: Boolean, deadline: String) -> Unit
) : DialogFragment() {

    @Inject
    lateinit var factory: DialogReactDislikeViewModel.Factory

    //todo try replace by viewBinding
    private lateinit var binding: DialogReasonRejectionCustomBinding

    val viewModel by viewModelCreator { factory.create(abilityCreateTask, id) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DialogReasonRejectionCustomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun setupListeners() {

        setDateSelection(
            binding.tvDeadline, parentFragmentManager, showClearView = false, underlineIfNull = true
        ) {
            it?.let {
                viewModel.setDeadline(it)
            }
        }

        binding.cbCreateTask.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onChangeCreateTask(isChecked)
        }

        binding.etDislikeReason.doAfterTextChanged {
            binding.etDislikeReason.setBackgroundResource(
                if (it.toString()
                        .isBlank()
                ) R.drawable.bg_underline_red else R.drawable.bg_8dp_white_border_gray_200
            )
        }

        binding.btnOk.setOnClickListener {

            val errors = errors()
            val viewState = viewModel.viewState

            if (errors.isNotEmpty()) dialogErrors(requireContext(), errors)
            else {

                taskCreator(
                    binding.etDislikeReason.text.toString(),
                    viewState.createTask,
                    if (viewState.deadline !=null) viewState.deadline.dateToJsonString() else Calendar.getInstance().time.dateToJsonString()
                )
                dismiss()
            }
        }

        binding.vResult.setTryAgainAction { viewModel.reload() }

    }

    private fun errors(): List<Int> {

        val errorsList = mutableListOf<Int>()

        if (binding.etDislikeReason.text.isBlank()) errorsList.add(R.string.dislike_reason)
        if (binding.tvDeadline.text.isBlank() && viewModel.viewState.createTask) errorsList.add(
            R.string.deadline
        )

        return errorsList
    }

    private fun renderState() {

        val viewState = viewModel.viewState

        with(binding) {
            designByViewState(
                viewState as BaseViewState, binding.root, binding.vResult
            )

            designedDateView(
                tvDeadline,
                viewState.deadline,
                showClearView = false,
                underlineIfNull = true
            )

            tvDeadline.text = viewState.deadline?.dateToString()
            cbCreateTask.isChecked = viewState.createTask
            if (viewState.createTask) tvDeadline.visible() else tvDeadline.gone()
            if (viewState.abilityCreateTask) cbCreateTask.visible() else cbCreateTask.gone()
        }
    }

    companion object {

        fun newInstance(
            abilityCreateTask: Boolean = false,
            id: String? = null,
            taskCreator: (reason: String, createDislikeTask: Boolean, deadline: String) -> Unit
        ) = DialogReactDislikeFragment(abilityCreateTask, id, taskCreator)

    }
}