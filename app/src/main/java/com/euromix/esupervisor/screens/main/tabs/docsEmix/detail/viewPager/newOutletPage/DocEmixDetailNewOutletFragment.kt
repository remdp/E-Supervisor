package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.databinding.DocEmixDetailNewOutletFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newPartnerPage.DocEmixDetailNewPartnerFragment
import dagger.hilt.android.AndroidEntryPoint

class DocEmixDetailNewOutletFragment :
    BaseFragment(R.layout.doc_emix_detail_new_outlet_fragment) {

    override val viewModel by viewModels<DocEmixDetailNewOutletViewModel>()

    private lateinit var binding: DocEmixDetailNewOutletFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DocEmixDetailNewOutletFragmentBinding.bind(view)

        viewModel.viewState.observe(viewLifecycleOwner) {

            with(binding) {

                tvOutletName.text = it.outletName
                tvInstitutionType.text = it.institutionType
                tvInstitutionNumber.text = it.institutionNumber
                tvGeneralOutlet.text = it.generalOutlet
                swNewGeneralOutlet.isChecked = it.newGeneralOutlet == true
                tvOutletFormat.text = it.outletFormat
                tvLocality.text = it.locality
                tvStreet.text = it.street
                tvHouseNumber.text = it.houseNumber
            }
        }

        viewModel.setViewState(arguments?.get(VIEW_STATE) as ViewState)

    }

    companion object {
        fun newInstance(docEmixDetail: DocEmixDetail): DocEmixDetailNewOutletFragment =
            DocEmixDetailNewOutletFragment().apply {
                arguments = bundleOf(
                    VIEW_STATE to ViewState(
                        docEmixDetail.outletName,
                        docEmixDetail.institutionType,
                        docEmixDetail.institutionNumber,
                        docEmixDetail.generalOutlet,
                        docEmixDetail.newGeneralOutlet,
                        docEmixDetail.outletFormat,
                        docEmixDetail.locality,
                        docEmixDetail.street,
                        docEmixDetail.houseNumber
                    )
                )
            }

        private const val VIEW_STATE = "view_state"
    }
}