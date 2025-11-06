package com.euromix.esupervisor.screens.main.tabs.filter

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.FilterFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilterFragment : BaseFragment(R.layout.filter_fragment) {

    private val navController: NavController by lazy { findNavController() }
    private val binding by viewBinding<FilterFragmentBinding>()
    private val args by navArgs<FilterFragmentArgs>()

    private val sharedViewModel: SharedFilterViewModel by lazy {
        navGraphViewModels<SharedFilterViewModel>(args.graphId).value
    }

    override val viewModel by viewModels<FilterViewModel>()

    private val filterAdapter by lazy {
        FilterItemsAdapter(
            args.filterTitles,
            onSearchTextChanged = { position, newText ->
                viewModel.onSearchTextChanged(position, newText)
            },
            onDetailItemMarked = { parentPosition, updatedDetailItem ->
                viewModel.onDetailItemMarked(parentPosition, updatedDetailItem)
            },
            onAllItemsChecked = { position, isChecked ->
                viewModel.onAllItemsChecked(position, isChecked)
            }
        )
    }

    @Inject
    lateinit var resManager: ResourceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFilter.adapter = filterAdapter

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener {

            val filterValidationEvent = sharedViewModel.validateFilter(viewModel.getFilter())

            when (filterValidationEvent) {
                is FilterValidationEvent.FilterValid -> {
                    sharedViewModel.postFilterResult(viewModel.getSelection())
                    navController.popBackStack()
                }

                is FilterValidationEvent.FilterNotValid -> showErrors(
                    filterValidationEvent.title,
                    filterValidationEvent.errorMessage
                )
            }
        }

        binding.tvClear.setOnClickListener {
            viewModel.onClearFilter()
        }

        binding.btnCancel.setOnClickListener {
            navController.popBackStack()
        }
        binding.vResult.setTryAgainAction { viewModel.loadFilterData() }
        binding.srl.setOnRefreshListener { viewModel.loadFilterData()  }
        binding.ivArrowBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    fun setupObservers() {
        renderState()
        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
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

        binding.tvClear.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (viewModel.filterIsClear()) R.drawable.ic_gray_basket else R.drawable.ic_blue_basket,
            0,
            0,
            0
        )
        binding.tvClear.setTextColor(
            if (viewModel.filterIsClear()) resManager.getColor(R.color.gray_300) else resManager.getColor(
                R.color.blue
            )
        )

        if (!viewState.isLoading && viewState.error == null) {
            addFlags()
            filterAdapter.submitList(viewState.selection.items)
            binding.srl.isEnabled = viewModel.initialData == null
        }
    }

    private fun addFlags() {

        with(binding) {

            llFlags.removeAllViews()

            for ((countFlagsTitles, _) in viewModel.viewState.selection.flags.withIndex()) {

                val mCheckBox = MaterialCheckBox(context).apply {

                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text =
                        args.flagsTitles?.getOrNull(countFlagsTitles) ?: "No matching title found"

                    isChecked = viewModel.viewState.selection.flags[countFlagsTitles].flag

                    setPadding(8, 0, 0, 0)
                    setButtonIconDrawableResource(R.drawable.checkbox_selector)

                    setOnCheckedChangeListener { _, isChecked ->
                        viewModel.onFlagChecked(countFlagsTitles, isChecked)
                    }
                }
                llFlags.addView(mCheckBox)
            }
        }
    }

    private fun showErrors(title: String, message: String) {

        val builder = AlertDialog.Builder(requireContext())

        val dialog = with(builder) {
            setTitle(title)
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
}