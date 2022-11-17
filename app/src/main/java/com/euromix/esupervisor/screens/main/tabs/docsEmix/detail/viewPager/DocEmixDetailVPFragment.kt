package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.databinding.DocEmixDetailVpFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocEmixDetailVPFragment() : BaseFragment(R.layout.doc_emix_detail_vp_fragment) {

    @Inject
    lateinit var factory: DocEmixDetailVPViewModel.Factory
    override val viewModel by viewModelCreator { factory.create(arguments?.get(DETAIL_DATA) as DocEmixDetail) }

    private lateinit var binding: DocEmixDetailVpFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DocEmixDetailVpFragmentBinding.bind(view)

        viewModel.detailData.observe(viewLifecycleOwner) {

            arguments?.let {
                setupViewPager(it[DETAIL_DATA] as DocEmixDetail)
            }
        }
    }

    private fun setupViewPager(docEmixDetail: DocEmixDetail) {
        binding.vpTabs.adapter = VPFragmentAdapter(this, docEmixDetail)
    }

    companion object {
        fun newInstance(docEmixDetail: DocEmixDetail): DocEmixDetailVPFragment =
            DocEmixDetailVPFragment().also { it.arguments = bundleOf(DETAIL_DATA to docEmixDetail) }

        private const val DETAIL_DATA = "detail_data"
        const val NEW_PARTNER_PAGE = 0
            const val NEW_OUTLET_PAGE = 1
    }
}