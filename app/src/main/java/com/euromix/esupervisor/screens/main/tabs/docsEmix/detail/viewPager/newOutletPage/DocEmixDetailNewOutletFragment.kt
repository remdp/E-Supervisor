package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.newOutletPage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.VIEW_STATE
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixDetailNewOutletFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.DocEmixDetailFragmentDirections

class DocEmixDetailNewOutletFragment :
    BaseFragment(R.layout.doc_emix_detail_new_outlet_fragment) {

    override val viewModel by viewModels<DocEmixDetailNewOutletViewModel>()
    private val binding by viewBinding<DocEmixDetailNewOutletFragmentBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivLocation.setOnClickListener {

            val viewState = viewModel.viewState.value

            viewState?.let {
                val direction =
                    DocEmixDetailFragmentDirections.actionDocEmixDetailFragmentToMapFragment(
                        titleData = TitleData(
                            getString(R.string.latitude, viewState.latitude) + "  " +
                                    getString(R.string.longitude, viewState.longitude), null
                        ),
                        latitude = viewState.latitude.toFloat(),
                        longitude = viewState.longitude.toFloat()
                    )
                findNavController().navigate(direction)
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { renderState(it) }
        arguments?.parcelable<ViewState>(VIEW_STATE)?.let { viewModel.setViewState(it) }
    }

    private fun renderState(state: ViewState) {
        with(binding) {
            tvOutletName.text = state.outletName
            tvInstitutionType.text = state.institutionType
            tvOutletFormat.text = state.outletFormat
            renderVisitsTextViews(state)
            tvVisitsFrequency.text = state.visitsFrequency
            tvLongitude.text = getString(R.string.longitude, state.longitude)
            tvLatitude.text = getString(R.string.latitude, state.latitude)
        }
    }

    private fun renderVisitsTextViews(viewState: ViewState) {

        with(binding) {

            viewState.visitDays?.let { days ->
                val textViews = arrayOf(
                    tvVisitDay1Label,
                    tvVisitDay2Label,
                    tvVisitDay3Label,
                    tvVisitDay4Label,
                    tvVisitDay5Label,
                    tvVisitDay6Label,
                    tvVisitDay7Label
                )

                for (i in 0..6) {
                    if (days.size > i && textViews.size > i) {
                        if (days[i]) {
                            textViews[i].setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_check_blue, 0, 0, 0
                            )
                            textViews[i].setTextColor(
                                getColor(requireContext(), R.color.blue)
                            )
                        }
                    }
                }
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
                        visitDays = docEmixDetail.visitDays,
                        visitsFrequency = docEmixDetail.visitsFrequency,
                        latitude = docEmixDetail.latitude,
                        longitude = docEmixDetail.longitude
                    )
                )
            }
    }
}