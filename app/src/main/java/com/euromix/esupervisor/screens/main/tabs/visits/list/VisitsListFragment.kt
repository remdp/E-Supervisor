package com.euromix.esupervisor.screens.main.tabs.visits.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.VisitsListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage.ImageAdapter
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage.ImageViewModel
import com.euromix.esupervisor.screens.main.tabs.docsEmix.list.DocsEmixListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitsListFragment : BaseFragment(R.layout.visits_list_fragment) {

    override val viewModel by viewModels<VisitsListViewModel>()

    private val binding by viewBinding<VisitsListFragmentBinding>()

    private val adapter = VisitsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers()
    }


    fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner){

            designByViewState(
                it as BaseViewState,
                binding.root,
                binding.vResult
            )
            renderState(it)

        }

    }


    private fun renderState(viewState: VisitsListViewModel.ViewState) {

        adapter.submitList(viewState.items)

    }

}