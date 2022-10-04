package com.euromix.esupervisor.screens.main.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.euromix.esupervisor.R
import com.euromix.esupervisor.databinding.FragmentTabsBinding

class TabsFragment : Fragment(R.layout.fragment_tabs) {

    private lateinit var binding: FragmentTabsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTabsBinding.bind(view)

        val navhost = childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        val navController = navhost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

}