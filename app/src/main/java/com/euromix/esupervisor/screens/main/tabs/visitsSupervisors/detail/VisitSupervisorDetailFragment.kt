package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.detail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.tasks.entities.Task
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.StoreCheckRow
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisorDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.VisitSupervisorDetailFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.main.tabs.tasks.list.TasksAdapter
import com.euromix.esupervisor.screens.viewModelCreator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VisitSupervisorDetailFragment : BaseFragment(R.layout.visit_supervisor_detail_fragment) {

    @Inject
    lateinit var factory: VisitSupervisorDetailViewModel.Factory
    private val navController: NavController by lazy { findNavController() }
    private val args by navArgs<VisitSupervisorDetailFragmentArgs>()
    override val viewModel by viewModelCreator { factory.create(args.id) }
    private val binding by viewBinding<VisitSupervisorDetailFragmentBinding>()

    @Inject
    lateinit var resManager: ResourceManager

    private val storeCheckAdapter = VisitSupervisorStoreCheckAdapter(::toStoreCheckDetail)
    private val tasksAdapter = TasksAdapter(false, ::toTaskDetail)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvStoreChecks.adapter = storeCheckAdapter
        binding.rvTasks.adapter = tasksAdapter

        viewModel.reload()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() = with(binding) {

        tvCheckIn.setOnClickListener { navigateToCheckIn() }

        tvCheckOut.setOnClickListener { viewModel.viewState.detailData?.let { handleCheckOutClick(it) } }

        tvStoreCheckEnd.setOnClickListener {
            handleConditionalClick(viewModel.viewState.detailData) {
                viewModel.expandStoreChecks()
            }
        }

        tvTasksEnd.setOnClickListener {
            handleConditionalClick(viewModel.viewState.detailData) {
                viewModel.expandTasks()
            }
        }
    }

    private fun setupObservers() {
        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun renderState() {

        val viewState = viewModel.viewState

        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult
        )

        if (!viewState.isLoading && viewState.error == null) {

            val detail = viewState.detailData ?: return
            binding.tvOutlet.text = detail.outlet
            binding.tvNumberStoreChecks.text = detail.storeChecks.size.toString()
            binding.tvNumberTasks.text = detail.tasks.size.toString()

            styleStatusTextView(
                binding.tvCheckIn,
                detail.isCheckIn
            )
            styleStatusTextView(
                binding.tvCheckOut,
                detail.isCheckOut
            )

            styleExpandableSection(
                binding.tvStoreCheckEnd,
                binding.rvStoreChecks,
                viewState.storeChecksExpand,
                detail.storeChecks.isNotEmpty()
            )
            styleExpandableSection(
                binding.tvTasksEnd,
                binding.rvTasks,
                viewState.tasksExpand,
                detail.tasks.isNotEmpty()
            )

            storeCheckAdapter.submitList(detail.storeChecks)
            tasksAdapter.submitList(detail.tasks)

        }
    }

    private fun navigateToCheckIn() {
        navController.navigate(
            VisitSupervisorDetailFragmentDirections.actionVisitSupervisorDetailFragmentToVisitSupervisorCheckInFragment(
                id = args.id,
                titleData = TitleData(getString(R.string.check_in))
            )
        )
    }

    private fun handleCheckOutClick(detail: VisitSupervisorDetail) {
        if (detail.isCheckIn)
            navController.navigate(
                VisitSupervisorDetailFragmentDirections.actionVisitSupervisorDetailFragmentToVisitSupervisorCheckOutFragment(
                    id = args.id,
                    titleData = TitleData(getString(R.string.check_out))
                )
            )
        else
            showCheckInNecessaryDialog()
    }

    private inline fun handleConditionalClick(data: VisitSupervisorDetail?, action: () -> Unit) {
        if (data?.isCheckIn == true) {
            action()
        } else {
            showCheckInNecessaryDialog()
        }
    }

    private fun styleStatusTextView(
        textView: TextView,
        isChecked: Boolean
    ) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            if (isChecked) R.drawable.ic_check_circle_green else R.drawable.ic_check_circle_gray,
            0,
            R.drawable.ic_arrow_right_gray,
            0
        )
        textView.setTextColor(resManager.getColor(if (isChecked) R.color.green_primary else R.color.black))
    }

    private fun styleExpandableSection(
        textView: TextView,
        recyclerView: RecyclerView,
        isExpanded: Boolean,
        hasData: Boolean
    ) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            0, 0,
            if (hasData) {
                if (isExpanded) R.drawable.ic_arrow_drop_up_gray else R.drawable.ic_arrow_drop_down_gray
            } else 0,
            0
        )
        recyclerView.visibility(isExpanded)
    }

    private fun toStoreCheckDetail(storeCheckRow: StoreCheckRow) {

        val titleStart = "${resManager.getString(R.string.store_check)} ${storeCheckRow.number}"
        val titleEnd = storeCheckRow.date.toText()

        if (storeCheckRow.levelsNumber == 1)
            navController.navigate(
                VisitSupervisorDetailFragmentDirections.actionVisitSupervisorDetailFragmentToStoreCheckLowerLevelFragment(
                    id = storeCheckRow.storeCheck.id,
                    titleData = TitleData(titleStart, titleEnd),
                    topLevelId = null,
                    twoLevels = false
                )
            )
        else
            navController.navigate(
                VisitSupervisorDetailFragmentDirections.actionVisitSupervisorDetailFragmentToStoreCheckTopLevelFragment(
                    id = storeCheckRow.storeCheck.id,
                    titleData = TitleData(titleStart, titleEnd),
                    twoLevels = true
                )
            )
    }

    private fun toTaskDetail(task: Task) {

        navController.navigate(
            VisitSupervisorDetailFragmentDirections.actionVisitSupervisorDetailFragmentToTaskDetailFragment(
                task.extId,
                TitleData(task.number, task.date.toText())
            )
        )
    }

    private fun showCheckInNecessaryDialog() =
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resManager.getString(R.string.check_in_necessary))
            .setPositiveButton(android.R.string.ok, null)
            .show()

}