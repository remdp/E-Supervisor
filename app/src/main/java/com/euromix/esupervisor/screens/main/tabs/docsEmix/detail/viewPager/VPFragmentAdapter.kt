package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.DocEmixDetailFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage.DocEmixDetailNewOutletFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newPartnerPage.DocEmixDetailNewPartnerFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.picturePage.DocEmixDetailPictureFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.returnRequestPage.DocEmixDetailReturnRequestFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage.DocEmixDetailTradeConditionFragment

class VPFragmentAdapter(fragment: Fragment, val docEmixDetail: DocEmixDetail) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = itemCount()

    override fun createFragment(position: Int): Fragment {

        return when (docEmixDetail.operationType) {

            DocEmixOperationType.ChangeTC -> createFragmentChangeTC(position)
            DocEmixOperationType.NewPartnerFact -> createFragmentNewPartnerFact(position)
            DocEmixOperationType.ReturnRequest -> createFragmentReturnRequest(position)
            else -> Fragment(R.layout.empty_fragment)
        }
    }

    private fun createFragmentChangeTC(position: Int): Fragment {

        return when (position) {
            CHANGE_TC_FRAGMENTS_COUNT - 1 -> DocEmixDetailTradeConditionFragment.newInstance(docEmixDetail.rowsTradeConditions)
            else -> createFragmentPicture(position - CHANGE_TC_FRAGMENTS_COUNT)
        }
    }

    private fun createFragmentNewPartnerFact(position: Int): Fragment {
        return when (position) {
            DocEmixDetailFragment.NEW_PARTNER_PAGE -> DocEmixDetailNewPartnerFragment.newInstance(
                docEmixDetail
            )
            DocEmixDetailFragment.NEW_OUTLET_PAGE -> DocEmixDetailNewOutletFragment.newInstance(
                docEmixDetail
            )
            DocEmixDetailFragment.TC_PAGE -> DocEmixDetailTradeConditionFragment.newInstance(
                docEmixDetail.rowsTradeConditions
            )
            else -> createFragmentPicture(position - NEW_PARTNER_FACT_FRAGMENTS_COUNT)
        }
    }

    private fun createFragmentReturnRequest(position: Int): Fragment {

        return when (position) {
            RETURN_REQUEST_FRAGMENTS_COUNT - 1 -> docEmixDetail.rowsReturnRequest?.let {
                DocEmixDetailReturnRequestFragment.newInstance(
                    it
                )
            } ?: Fragment(R.layout.empty_fragment)
            else -> createFragmentPicture(position - RETURN_REQUEST_FRAGMENTS_COUNT)
        }
    }

    private fun createFragmentPicture(position: Int): Fragment {
        return docEmixDetail.picturesPaths?.get(position)?.path?.let {
            DocEmixDetailPictureFragment.newInstance(it)
        } ?: Fragment(R.layout.empty_fragment)
    }

    private fun itemCount(): Int {

        return when (docEmixDetail.operationType) {
            DocEmixOperationType.ChangeTC -> CHANGE_TC_FRAGMENTS_COUNT
            DocEmixOperationType.NewPartnerFact -> NEW_PARTNER_FACT_FRAGMENTS_COUNT + picturesCount()
            DocEmixOperationType.ReturnRequest -> RETURN_REQUEST_FRAGMENTS_COUNT + picturesCount()
            else -> 0
        }
    }

    private fun picturesCount(): Int = (docEmixDetail.picturesPaths?.size ?: 0)

    companion object {

        const val CHANGE_TC_FRAGMENTS_COUNT = 1
        const val NEW_PARTNER_FACT_FRAGMENTS_COUNT = 3
        const val RETURN_REQUEST_FRAGMENTS_COUNT = 1

    }
}