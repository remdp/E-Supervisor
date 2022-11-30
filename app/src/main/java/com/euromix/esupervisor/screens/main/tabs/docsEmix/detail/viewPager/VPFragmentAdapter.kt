package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.DocEmixDetailFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage.DocEmixDetailNewOutletFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newPartnerPage.DocEmixDetailNewPartnerFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage.DocEmixDetailTradeConditionFragment

class VPFragmentAdapter(fragment: Fragment, val docEmixDetail: DocEmixDetail) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return if (docEmixDetail.operationType == DocEmixOperationType.ChangeTC) 1
        else 3
    }

    override fun createFragment(position: Int): Fragment {

        return when (docEmixDetail.operationType) {

            DocEmixOperationType.ChangeTC -> DocEmixDetailTradeConditionFragment.newInstance(
                docEmixDetail.rowTradeConditions
            )
            DocEmixOperationType.NewPartnerFact -> {
                when (position) {
                    DocEmixDetailFragment.NEW_PARTNER_PAGE -> DocEmixDetailNewPartnerFragment.newInstance(
                        docEmixDetail
                    )
                    DocEmixDetailFragment.NEW_OUTLET_PAGE -> DocEmixDetailNewOutletFragment.newInstance(
                        docEmixDetail
                    )
                    DocEmixDetailFragment.TC_PAGE -> DocEmixDetailTradeConditionFragment.newInstance(
                        docEmixDetail.rowTradeConditions
                    )
                    else -> Fragment(R.layout.empty_fragment)
                }
            }
            else -> Fragment(R.layout.empty_fragment)
        }
    }
}