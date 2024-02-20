package com.euromix.esupervisor.screens.main.tabs

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.FragmentTabsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabsFragment : BaseFragment(R.layout.fragment_tabs) {

    private val binding by viewBinding<FragmentTabsBinding>()

    override val viewModel by viewModels<TabsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        val navController = navHostFragment.navController

        if (viewModel.getCurrentRole() != Role.SUPERVISOR) {
            val navGraph = navController.navInflater.inflate(R.navigation.tabs_graph)
            navGraph.setStartDestination(R.id.rates_graph)
            navController.graph = navGraph
            binding.bottomNavigationView.selectedItemId = R.id.rates_graph
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.additional_menu_item -> {
                    val popupMenu =
                        PopupMenu(requireContext(), binding.bottomNavigationView, Gravity.END)
                    popupMenu.menuInflater.inflate(R.menu.additional_menu, popupMenu.menu)
                    popupMenu.show()

                    popupMenu.setOnMenuItemClickListener {
                        NavigationUI.onNavDestinationSelected(it, navController)
                    }
                }
                else -> {
                      NavigationUI.onNavDestinationSelected(menuItem, navController)
                }
            }
            return@setOnItemSelectedListener true
        }
    }
}