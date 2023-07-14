package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.tasks.entities.TasksCreateOutletsSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.OutletsTaskSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OutletsSelectionFragment : BaseFragment(R.layout.outlets_task_selection_fragment) {

    private val navController: NavController by lazy { findNavController() }
    private val binding by viewBinding<OutletsTaskSelectionFragmentBinding>()

    override val viewModel by viewModels<OutletsSelectionViewModel>()

    private val searchAdapter by lazy { OutletsSearchItemsAdapter(viewModel, viewLifecycleOwner) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSearchSelectionItems.adapter = searchAdapter

        setupListeners()
        setupObservers(view)

    }

    private fun setupListeners() {
        binding.btnOk.setOnClickListener {
            if (viewModel.verifyTA()) returnToCreationScreen(true)
            else showErrors()
        }

        binding.btnCancel.setOnClickListener { returnToCreationScreen() }
        binding.vResult.setTryAgainAction { viewModel.searchSelectionsForCreateTasks() }
        binding.ivArrowBack.setOnClickListener { returnToCreationScreen() }
    }

    private fun returnToCreationScreen(pressOk: Boolean = false) {

        navController.previousBackStackEntry?.savedStateHandle?.set(
            "key",
            if (pressOk) viewModel.outletsSelection() else null
        )
        navController.popBackStack()

    }

    fun setupObservers(view: View) {
        viewModel.searchSelections.observeResults(this, view, binding.vResult) { searchSelections ->
            searchAdapter.searchItems = searchSelections
        }
    }

    private fun showErrors() {

        val builder = AlertDialog.Builder(requireContext())

        val dialog = with(builder) {
            setTitle(R.string.validation_errors)
            setMessage(getString(R.string.need_select_ta))
            setPositiveButton("OK", null)
            create()
        }

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

}