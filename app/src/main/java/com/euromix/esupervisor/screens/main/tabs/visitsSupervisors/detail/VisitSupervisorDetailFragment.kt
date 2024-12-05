package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.detail

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.VisitSupervisorDetailFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.viewModelCreator
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderState()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        binding.btnCheckIn.setOnClickListener {

            navController.navigate(
                VisitSupervisorDetailFragmentDirections.actionVisitSupervisorDetailFragmentToVisitSupervisorCheckInFragment(
                    args.id,
                    TitleData("Check - in"),
                    viewModel.viewState.detailData?.outlet ?: ""
                )
            )
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

            binding.tvOutlet.text = viewState.detailData?.outlet

        }


    }

}