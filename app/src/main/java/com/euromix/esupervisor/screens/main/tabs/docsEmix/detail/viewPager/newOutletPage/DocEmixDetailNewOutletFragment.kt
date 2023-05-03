package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.databinding.DocEmixDetailNewOutletFragmentBinding

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
                tvOutletFormat.text = it.outletFormat
                designVisitsTextViews(it)
                tvVisitsFrequency.text = it.visitsFrequency

            }
        }
        arguments?.parcelable<ViewState>(VIEW_STATE)?.let { viewModel.setViewState(it) }
    }

    private fun designVisitsTextViews(viewState: ViewState) {

        with(binding) {

            if (viewState.visitDay1) {
                tvVisitDay1Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay1Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }

            if (viewState.visitDay2) {
                tvVisitDay2Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay2Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }

            if (viewState.visitDay3) {
                tvVisitDay3Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay3Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }

            if (viewState.visitDay4) {
                tvVisitDay4Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay4Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }

            if (viewState.visitDay5) {
                tvVisitDay5Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay5Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }

            if (viewState.visitDay6) {
                tvVisitDay6Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay6Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }

            if (viewState.visitDay7) {
                tvVisitDay7Label.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check_blue, 0, 0, 0
                )
                tvVisitDay7Label.setTextColor(
                    getColor(requireContext(), R.color.blue)
                )
            }
        }
    }

    companion object {
        fun newInstance(docEmixDetail: DocEmixDetail): DocEmixDetailNewOutletFragment =
            DocEmixDetailNewOutletFragment().apply {
                arguments = bundleOf(
                    VIEW_STATE to ViewState(
                        outletName = docEmixDetail.outletName,
                        institutionType = docEmixDetail.institutionType,
                        outletFormat = docEmixDetail.outletFormat,
                        visitDay1 = docEmixDetail.visitDay1,
                        visitDay2 = docEmixDetail.visitDay2,
                        visitDay3 = docEmixDetail.visitDay3,
                        visitDay4 = docEmixDetail.visitDay4,
                        visitDay5 = docEmixDetail.visitDay5,
                        visitDay6 = docEmixDetail.visitDay6,
                        visitDay7 = docEmixDetail.visitDay7,
                        visitsFrequency = docEmixDetail.visitsFrequency
                    )
                )
            }

        private const val VIEW_STATE = "VIEW_STATE"
    }
}