package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.sales

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.StoreCheckSalesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VisitsSupervisorSalesFragment: Fragment(R.layout.store_check_sales_fragment) {

    private val binding by viewBinding<StoreCheckSalesFragmentBinding>()
    private val args by navArgs<VisitsSupervisorSalesFragmentArgs>()

    @Inject
    lateinit var adapter: VisitsSupervisorSalesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // val adapter = VisitsSupervisorSalesAdapter()
        binding.rvSales.adapter = adapter
        adapter.submitList(args.sales.toList())

    }
}