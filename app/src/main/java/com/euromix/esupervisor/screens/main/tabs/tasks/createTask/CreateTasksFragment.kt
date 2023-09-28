package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.tasks.entities.TasksCreateOutletsSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ActivitySubscription
import com.euromix.esupervisor.app.utils.addSoftKeyboardVisibilityListener
import com.euromix.esupervisor.app.utils.designedDateView
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.setDateSelection
import com.euromix.esupervisor.app.utils.setDrawableOnClickListener
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.CreateTasksFragmentBinding
import com.euromix.esupervisor.databinding.ItemOutletCreateTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTasksFragment : BaseFragment(R.layout.create_tasks_fragment) {

    private val navController: NavController by lazy { findNavController() }

    override val viewModel by viewModels<CreateTaskViewModel>()

    private val binding by viewBinding<CreateTasksFragmentBinding>()

    private val outletsAdapter by lazy { OutletsAdapter(viewModel) }
    private lateinit var taskTypesAdapter: TaskTypesAdapter

    private lateinit var keyboardSubscription: ActivitySubscription

    private var collapseOutlets = false

    override fun onResume() {
        super.onResume()

        keyboardSubscription = addSoftKeyboardVisibilityListener {
            if (it) {
                binding.btnOk.gone()
                binding.btnCancel.gone()
            } else {
                binding.btnOk.visible()
                binding.btnCancel.visible()
            }
        }

        setupObservers(binding.root)

        binding.etSearch.text?.clear()
    }

    override fun onPause() {
        super.onPause()
        keyboardSubscription.dispose()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskTypesAdapter = TaskTypesAdapter(requireContext())
        binding.rvSelectionItems.adapter = outletsAdapter

        setDateSelection(
            binding.tvDeadline,
            parentFragmentManager,
            showClearView = true,
            underlineIfNull = true
        ) {
            viewModel.deadline = it
            designViews()
        }

        setupListeners()
        observeNavigationCallBack()

        designViews()
    }

    private fun observeNavigationCallBack() {

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<TasksCreateOutletsSelection?>(
            "key"
        )?.observe(viewLifecycleOwner) {
            if (it != null) viewModel.updateOutletsSelection(it)
        }
    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener {

            val description = binding.tvDescription.text.toString()

            val errors = viewModel.verifyPossibilityCreation(description)
            if (errors.isNotEmpty()) {
                showErrors(errors)
            } else {
                viewModel.createTasks(description)
            }
        }
        binding.btnCancel.setOnClickListener { navController.popBackStack() }

        binding.ivFunnel.setOnClickListener {
            navController.navigate(CreateTasksFragmentDirections.actionCreateTaskFragmentToOutletsSelectionFragment())
        }

        binding.etSearch.doAfterTextChanged { text ->
            viewModel.filteredBy(text)
            binding.cbOutlets.setButtonIconDrawableResource(
                viewModel.drawableForParentCheckBox()
            )
            binding.btnOk.gone()
            binding.btnCancel.gone()
        }

        binding.cbOutlets.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeMark(
                isChecked
            )

            binding.rvSelectionItems.forEach {
                val itemBinding = ItemOutletCreateTaskBinding.bind(it)
                itemBinding.cbPartner.setButtonDrawable(
                    viewModel.drawableForChildCheckBox(
                        isChecked
                    )
                )
            }

            binding.cbOutlets.setButtonIconDrawableResource(
                viewModel.drawableForParentCheckBox()
            )
        }

        binding.cbAttachPhoto.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.cbAttachPhoto.setButtonIconDrawableResource(
                viewModel.drawableForChildCheckBox(isChecked)
            )
            viewModel.attachPhoto = isChecked
        }

        outletsAdapter.itemClickListener = { downLevelPos: Int, mark: Boolean ->

            viewModel.changeMark(mark, outletsAdapter.list[downLevelPos])

            binding.cbOutlets.setButtonIconDrawableResource(
                viewModel.drawableForParentCheckBox()
            )
        }

        binding.tvDescription.addTextChangedListener { designViews() }

        binding.tvOutlets.setDrawableOnClickListener { collapseOutlets() }

    }

    private fun setupObservers(view: View) {

        viewModel.outlets.observeResults(this, binding.root, binding.vResult) {
            outletsAdapter.list = it
            binding.cbOutlets.setButtonIconDrawableResource(
                viewModel.drawableForParentCheckBox()
            )
        }

        viewModel.outletsSelection.observeEvent(viewLifecycleOwner) {
            viewModel.findOutletsForCreateTask(it)
        }

        viewModel.foundTasksType.observeResults(this, view, binding.vResult) {
            binding.tvTaskType.setOnClickListenerLocalSelection(
                it,
                viewModel::updateChosenTaskType,
                viewModel::handleViewClick,
                viewModel::checkTaskTypeEmpty
            )
        }

        viewModel.chosenTasksType.observe(viewLifecycleOwner) {
            binding.tvTaskType.text = it?.presentation
            binding.tvTaskType.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )
            designViews()
        }

        viewModel.tasksCreationResult.observeEvent(viewLifecycleOwner) {
            if (it is Success || it is Error) {
                //todo replace with simplyMessageDialog
                showResultCreationTasks(it)
                binding.vResult.setResult(this, it, false)
            } else binding.vResult.setResult(this, it, true)
        }
    }

    private fun showResultCreationTasks(result: Result<String>) {

        val message = if (result is Success) getString(
            R.string.tasks_creation_result, result.value
        ) else getString(R.string.tasks_creation_error)

        val builder = AlertDialog.Builder(requireContext())

        val dialog = with(builder) {
            setTitle(R.string.tasks_creation_result_title)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
        }

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    private fun showErrors(errors: List<Int>) {

        var message = getString(R.string.need_fill_colon)

        errors.forEach {
            message = message + "\n" + getString(it)
        }

        val builder = AlertDialog.Builder(requireContext())

        val dialog = with(builder) {
            setTitle(R.string.validation_errors)
            setMessage(message)
            setPositiveButton("OK", null)
            create()
        }

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    private fun designViews() {

//        designedDateView(
//            binding.tvDeadline, viewModel.deadline,
//            showClearView = true,
//            underlineIfNull = true
//        )
//        if (viewModel.deadline == null)
//            binding.tvDeadline.setBackgroundResource(R.drawable.bg_underline_red)
//        else binding.tvDeadline.setBackgroundResource(
//            R.drawable.bg_8dp_white_border
//        )

        if (viewModel.chosenTasksType.value == null)
            binding.tvTaskType.setBackgroundResource(R.drawable.bg_underline_red)
        else binding.tvTaskType.setBackgroundResource(
            R.drawable.bg_8dp_white_border
        )

        if (binding.tvDescription.text.toString().isBlank())
            binding.tvDescription.setBackgroundResource(R.drawable.bg_underline_red)
        else binding.tvDescription.setBackgroundResource(
            R.drawable.bg_8dp_white_border
        )
    }

    private fun collapseOutlets() {

        collapseOutlets = !collapseOutlets

        binding.tvOutlets.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            if (collapseOutlets) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_arrow_drop_up_gray_400,
            0
        )

        if (collapseOutlets) {
            binding.cbOutlets.gone()
            binding.ivFunnel.gone()
            binding.tiSearch.gone()
            binding.rvSelectionItems.gone()
        } else {
            binding.cbOutlets.visible()
            binding.ivFunnel.visible()
            binding.tiSearch.visible()
            binding.rvSelectionItems.visible()
        }
    }
}