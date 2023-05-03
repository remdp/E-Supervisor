package com.euromix.esupervisor.screens.main.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.databinding.FragmentTabsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabsFragment : BaseFragment(R.layout.fragment_tabs) {

    private lateinit var binding: FragmentTabsBinding

    override val viewModel by viewModels<TabsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTabsBinding.bind(view)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        if (viewModel.getCurrentRole() != Role.SUPERVISOR) {
            val navGraph = navController.navInflater.inflate(R.navigation.tabs_graph)
            navGraph.setStartDestination(R.id.rates_graph)
            navController.graph = navGraph
        }
    }
}