package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newPartnerPage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.databinding.DocEmixDetailNewPartnerFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage.DocEmixDetailTradeConditionFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage.DocEmixDetailTradeConditionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocEmixDetailNewPartnerFragment :
    BaseFragment(R.layout.doc_emix_detail_new_partner_fragment) {

    override val viewModel by viewModels<DocEmixDetailNewPartnerViewModel>()

    private lateinit var binding: DocEmixDetailNewPartnerFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DocEmixDetailNewPartnerFragmentBinding.bind(view)

        viewModel.viewState.observe(viewLifecycleOwner) {

            with(binding) {
                tvWorkingName.text = it.workingName
                tvInnerDSChannel.text = it.innerDS
                tvEDRPOU.text = it.edrpou
            }
        }

        arguments?.let {
            viewModel.setViewState(requireArguments()[VIEW_STATE] as ViewState)
        }
    }

    companion object {
        fun newInstance(docEmixDetail: DocEmixDetail): DocEmixDetailNewPartnerFragment =
            DocEmixDetailNewPartnerFragment().also { fragment ->

                fragment.arguments = bundleOf(
                    VIEW_STATE to ViewState(
                        docEmixDetail.workingName,
                        docEmixDetail.innerDistributionChannel,
                        docEmixDetail.edrpou
                    )
                )
            }

        private const val VIEW_STATE = "view_state"
    }
}