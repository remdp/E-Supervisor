package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.enums.FilterSource
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.entities.TasksCreateOutletsSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ActivitySubscription
import com.euromix.esupervisor.app.utils.addSoftKeyboardVisibilityListener
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.setDateSelection
import com.euromix.esupervisor.app.utils.setDrawableOnClickListener
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.showErrors
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.CreateTasksFragmentBinding
import com.euromix.esupervisor.databinding.ItemOutletCreateTaskBinding
import com.euromix.esupervisor.screens.main.tabs.filter.FilterValidationEvent
import com.euromix.esupervisor.screens.main.tabs.filter.SharedFilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTasksFragment : BaseFragment(R.layout.create_tasks_fragment) {

    private val navController: NavController by lazy { findNavController() }
    private val args by navArgs<CreateTasksFragmentArgs>()

    override val viewModel by viewModels<CreateTaskViewModel>()

    private val sharedViewModel: SharedFilterViewModel by lazy {
        navGraphViewModels<SharedFilterViewModel>(args.graphId).value
    }

    private val binding by viewBinding<CreateTasksFragmentBinding>()

    private val outletsAdapter by lazy { OutletsAdapter(viewModel) }
    private lateinit var taskTypesAdapter: TaskTypesAdapter

    private lateinit var keyboardSubscription: ActivitySubscription

    private var collapseOutlets = false

    private val hasStoreCheckId: Boolean by lazy {
        arguments?.containsKey(STORE_CHECK_ID) ?: false
    }

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

        sharedViewModel.setFilterValidator { filter ->

            val isValid = filter[0].detailFilterItems.any { it.marked }

            return@setFilterValidator if (isValid) {
                FilterValidationEvent.FilterValid
            } else {
                val errorTitle = getString(R.string.validation_errors)
                val errorMessage = getString(R.string.need_select_ta)
                FilterValidationEvent.FilterNotValid(errorTitle, errorMessage)
            }
        }

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
        viewModel.setStoreCheckId(storeCheckId())
        designViews()
    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener {

            val description = binding.tvDescription.text.toString()

            val errors = viewModel.verifyPossibilityCreation(description)
            if (errors.isNotEmpty()) {
                showErrors(requireContext(), errors)
            } else {
                viewModel.createTasks(description)
            }
        }
        binding.btnCancel.setOnClickListener { navController.popBackStack() }

        binding.ivFunnel.setOnClickListener {
            Log.d("NavDebug", "Current destination: ${navController.currentDestination?.label}")
            navController.navigate(
                CreateTasksFragmentDirections.actionCreateTaskFragmentToFilterFragment(
                    filterSource = FilterSource.FROM_CREATE_TASK_FRAGMENT, filterTitles = arrayOf(
                        getString(R.string.trading_agents),
                        getString(R.string.outlets_types)
                    ),
                    graphId = R.id.tasks_graph
                )
            )
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
        sharedViewModel.filterResult.observe(viewLifecycleOwner) { filter ->

            viewModel.updateOutletsSelection(
                TasksCreateOutletsSelection(
                    tradingAgents = filter.items[0].detailFilterItems.filter { it.marked }
                        .map { it.serverPair.id },
                    outletsInnerTypes = filter.items[1].detailFilterItems.filter { it.marked }
                        .map { it.serverPair.id })
            )
        }

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
                ::handleViewClick,
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
                .setOnClickListener {
                    dialog.dismiss()

                    if (result is Success) {
                        val resultBundle = Bundle().apply {
                            putBoolean(Const.BUNDLE_KEY_TASK_CREATION_SUCCESSFUL, true)
                        }
                        setFragmentResult(Const.REQUEST_TASK_CREATION, resultBundle)
                    }
                    navController.popBackStack()
                }
        }
        dialog.show()
    }

    private fun designViews() {
        if (viewModel.chosenTasksType.value == null)
            binding.tvTaskType.setBackgroundResource(R.drawable.bg_underline_red)
        else binding.tvTaskType.setBackgroundResource(
            R.drawable.bg_8dp_white_border_gray_200
        )

        if (binding.tvDescription.text.toString().isBlank())
            binding.tvDescription.setBackgroundResource(R.drawable.bg_underline_red)
        else binding.tvDescription.setBackgroundResource(
            R.drawable.bg_8dp_white_border_gray_200
        )

        if (hasStoreCheckId) {
            binding.tvPartner.text = arguments?.getString(PARTNER)
            binding.tvOutlet.text = arguments?.getString(OUTLET)
            binding.tvTradeAgent.text = arguments?.getString(TRADE_AGENT)
        }

        //manage visibility for outlets views
        binding.grOutletsFilterViews.visibility(!hasStoreCheckId)
        binding.grStoreCheckFilterViews.visibility(hasStoreCheckId)

    }

    private fun storeCheckId() = arguments?.getString(STORE_CHECK_ID)


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

    // 0-common click
    //1-right drawable click
    private fun handleViewClick(
        itemsList: List<ServerPair>,
        updaterSelection: (ServerPair?) -> Unit,
        anchor: View,
        click: Int,
        emptyChecker: () -> Boolean
    ) {

        if (click == 0 || emptyChecker())
            popupWindowForSelections(
                anchor.context,
                itemsList,
                updaterSelection
            ).showAsDropDown(anchor)
        else updaterSelection(null)

    }

    companion object {
        private const val STORE_CHECK_ID = "store_check_id"
        private const val PARTNER = "partner"
        private const val OUTLET = "outlet"
        private const val TRADE_AGENT = "tradeAgent"
    }
}