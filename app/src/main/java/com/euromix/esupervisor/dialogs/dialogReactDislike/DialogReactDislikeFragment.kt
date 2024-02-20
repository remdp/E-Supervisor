package com.euromix.esupervisor.dialogs.dialogReactDislike

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.dateToString
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.designedDateView
import com.euromix.esupervisor.app.utils.dialogErrors
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.setDateSelection
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.DialogReasonRejectionCustomBinding
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
    //private val binding by viewBinding<DialogReasonRejectionCustomBinding>()
    private lateinit var binding: DialogReasonRejectionCustomBinding

    //  private val viewModel by viewModels<DialogReactDislikeViewModel>()

//    val viewModel: DialogReactDislikeViewModel by viewModels {
//        DialogReactDislikeViewModelFactory(abilityCreateTask)
//    }

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

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderState(it)

            designByResult(
                it.deadline,
                binding.root,
                binding.vResult,
                specialViews = listOf(binding.tvDeadline, binding.cbCreateTask)
            )
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
                ) R.drawable.bg_underline_red else R.drawable.bg_8dp_white_border
            )
        }

        binding.btnOk.setOnClickListener {

            val errors = errors()
            val stateData = viewModel.viewState.value

            if (errors.isNotEmpty()) dialogErrors(requireContext(), errors)
            else {

                stateData?.let { stateData ->

                    taskCreator(
                        binding.etDislikeReason.text.toString(),
                        stateData.createTask,
                        if (stateData.deadline is Success) stateData.deadline.value.dateToJsonString() else Calendar.getInstance().time.dateToJsonString()
                    )
                }
                dismiss()
            }
        }

        binding.vResult.setTryAgainAction { viewModel.reload() }

    }

    private fun errors(): List<Int> {

        val errorsList = mutableListOf<Int>()

        if (binding.etDislikeReason.text.isBlank()) errorsList.add(R.string.dislike_reason)
        if (binding.tvDeadline.text.isBlank() && viewModel.viewState.value?.createTask == true) errorsList.add(
            R.string.deadline
        )

        return errorsList
    }

    private fun renderState(viewState: DialogReactDislikeViewModel.ViewState) {

        with(binding) {

            cbCreateTask.isChecked = viewState.createTask
            if (viewState.createTask) tvDeadline.visible() else tvDeadline.gone()
            if (viewState.abilityCreateTask) cbCreateTask.visible() else cbCreateTask.gone()

            if (viewState.deadline is Success) {
                tvDeadline.text = viewState.deadline.value.dateToString()
                designedDateView(
                    tvDeadline,
                    viewState.deadline.value,
                    showClearView = false,
                    underlineIfNull = true
                )
            }
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