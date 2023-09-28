package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage.ImagesFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage.DocEmixDetailNewOutletFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.returnRequestPage.DocEmixDetailReturnRequestFragment
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.tradeConditionPage.DocEmixDetailTradeConditionFragment

class VPFragmentAdapter(
    fragment: Fragment,
    private val docEmixDetail: DocEmixDetail,
    private val openerImageFragment: (imageUri: String) -> Unit
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = itemCount()

    override fun createFragment(position: Int): Fragment {

        return when (docEmixDetail.operationType) {

            DocEmixOperationType.ADD_TC -> createFragmentChangeTC()
            DocEmixOperationType.NEW_PARTNER_FACT -> createFragmentNewPartnerFact(position)
            DocEmixOperationType.RETURN_REQUEST -> createFragmentReturnRequest(position)
            else -> Fragment(R.layout.empty_fragment)
        }
    }

    private fun createFragmentChangeTC(): Fragment =
        DocEmixDetailTradeConditionFragment.newInstance(docEmixDetail.rowsTradeConditions)

    private fun createFragmentNewPartnerFact(position: Int): Fragment {
        return when (position) {

            0 -> DocEmixDetailNewOutletFragment.newInstance(docEmixDetail)
            1 -> DocEmixDetailTradeConditionFragment.newInstance(docEmixDetail.rowsTradeConditions)
            else -> createImagesFragment()
        }
    }

    private fun createFragmentReturnRequest(position: Int): Fragment {

        return when (position) {
            0 -> docEmixDetail.rowsReturnRequest?.let {
                DocEmixDetailReturnRequestFragment.newInstance(it)
            } ?: Fragment(R.layout.empty_fragment)

            else -> createImagesFragment()
        }
    }

    private fun createImagesFragment(): Fragment =
        if (docEmixDetail.images > 0)
            ImagesFragment.newInstance(
                extId = docEmixDetail.extId,
                abilityCreateTask = false,
                openerImageFragment = openerImageFragment
            )
        else Fragment(R.layout.empty_fragment)

    private fun itemCount(): Int {

        return when (docEmixDetail.operationType) {
            DocEmixOperationType.ADD_TC -> 1
            DocEmixOperationType.NEW_PARTNER_FACT -> 3
            DocEmixOperationType.RETURN_REQUEST -> 2
            else -> 0
        }
    }
}