package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.App.Companion.getDrawable
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.dialogPositiveButton
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.DocEmixDetailFragmentBinding
import com.euromix.esupervisor.databinding.TabHeaderBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.VPFragmentAdapter
import com.euromix.esupervisor.screens.viewModelCreator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocEmixDetailFragment : BaseFragment(R.layout.doc_emix_detail_fragment) {

    @Inject
    lateinit var factory: DocEmixDetailViewModel.Factory
    override val viewModel by viewModelCreator { factory.create(args.extId) }
    private val binding by viewBinding<DocEmixDetailFragmentBinding>()
    private val args by navArgs<DocEmixDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            viewModel.afterUpdateState()

            if (state.result is Success && !state.needLoading) {
                val docEmixDetail = state.result.value
                renderState(docEmixDetail)
                setupViewPager(docEmixDetail)
            }
            designByResult(
                state.result, binding.root, binding.vResult, null, listOf(binding.clAppbarBottom)
            )
        }
    }

    private fun setupListeners() {
        binding.btnApprove.setOnClickListener { viewModel.acceptDocEmixDetail() }
        binding.btnReject.setOnClickListener {
            dialogPositiveButton(requireContext(), R.string.reject_reason) { enteredText, _, _ ->
                viewModel.rejectDocEmixDetail(enteredText)
            }
        }
        binding.vResult.setTryAgainAction { viewModel.reload() }
    }

    private fun renderState(docEmixDetail: DocEmixDetail) {

        with(binding) {

            tvPartner.text = docEmixDetail.partner
            Status.designTV(tvStatus, docEmixDetail.status)
            DocEmixOperationType.designTV(
                tvOperationType, docEmixDetail.operationType, docEmixDetail.status, true
            )
            tvDescription.text = docEmixDetail.description

            tvTradingAgent.text = docEmixDetail.tradingAgent
            clAppbarBottom.isVisible = docEmixDetail.canBeAgreed

            if (docEmixDetail.operationType == DocEmixOperationType.NEW_PARTNER_FACT) {
                tvDistribChannelLabel.visible()
                tvDistribChannel.visible()
                tvEDRPOULabel.visible()
                tvEDRPOU.visible()
                tvPartner.text = docEmixDetail.workingName
                tvDistribChannel.text = docEmixDetail.innerDistributionChannel
                tvEDRPOU.text = docEmixDetail.edrpou
            }
        }
    }


    private fun setupViewPager(
        docEmixDetail: DocEmixDetail
    ) {
        with(binding) {

            vpTabs.adapter =
                VPFragmentAdapter(this@DocEmixDetailFragment, docEmixDetail) { imageUri ->
                    val direction =
                        DocEmixDetailFragmentDirections.actionDocEmixDetailFragmentToImageFragment(
                            imageUri = imageUri,
                            titleData = args.titleData
                        )
                    findNavController().navigate(direction)
                }

            if (docEmixDetail.operationType == DocEmixOperationType.NEW_PARTNER_FACT || docEmixDetail.images > 0) {
                addTabsListener(tlTabs)
                addTabsMediator(tlTabs, vpTabs, docEmixDetail)
                addDividerTabs(tlTabs)
                tlTabs.visible()
            } else {
                tlTabs.gone()
            }
        }
    }

    private fun addTabsListener(tlTabs: TabLayout) {

        tlTabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                tab.customView?.let {
                    val bindingTab = TabHeaderBinding.bind(it)
                    bindingTab.tvLeft.setTextColor(getColor(it.context, R.color.blue))

                    bindingTab.tvRight.setTextColor(getColor(it.context, R.color.blue))

                    bindingTab.tvRight.background =
                        getDrawable(it.context, R.drawable.bg_4dp_blue_10)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

                tab.customView?.let {
                    val bindingTab = TabHeaderBinding.bind(it)
                    bindingTab.tvLeft.setTextColor(getColor(it.context, R.color.gray_400))

                    bindingTab.tvRight.setTextColor(getColor(it.context, R.color.gray_400))
                    bindingTab.tvRight.background =
                        getDrawable(it.context, R.drawable.bg_4dp_dark_5)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun addTabsMediator(
        tlTabs: TabLayout, vpTabs: ViewPager2, docEmixDetail: DocEmixDetail
    ) {
        TabLayoutMediator(tlTabs, vpTabs) { tab, position ->

            val bindingTLHeader = TabHeaderBinding.inflate(layoutInflater, null, false)
            with(bindingTLHeader) {

                when (docEmixDetail.operationType) {
                    DocEmixOperationType.ADD_TC -> {
                        //todo if necessary
                    }

                    DocEmixOperationType.NEW_PARTNER_FACT -> {
                        when (position) {
                            0 -> {
                                tvLeft.text = App.getString(requireContext(), R.string.outlet)
                                tvRight.gone()
                            }

                            1 -> {
                                tvLeft.text = App.getString(requireContext(), R.string.tc_data)
                                tvRight.text =
                                    (if (docEmixDetail.rowsTradeConditions == null) 0 else docEmixDetail.rowsTradeConditions.size).toString()
                            }

                            else -> {
                                tvLeft.text = App.getString(requireContext(), R.string.photo)
                                tvRight.text = docEmixDetail.images.toString()
                            }
                        }
                    }

                    DocEmixOperationType.RETURN_REQUEST -> {
                        when (position) {
                            0 -> {
                                tvLeft.text = App.getString(requireContext(), R.string.goods)
                                tvRight.text =
                                    (if (docEmixDetail.rowsReturnRequest == null) 0 else docEmixDetail.rowsReturnRequest.size).toString()
                            }

                            else -> {
                                tvLeft.text = App.getString(requireContext(), R.string.gallery)
                                tvRight.text = docEmixDetail.images.toString()
                            }
                        }
                    }

                    else -> {}
                }
            }

            tab.customView = bindingTLHeader.root

        }.attach()
    }

    private fun addDividerTabs(tlTabs: TabLayout) {

        tlTabs.getChildAt(0).let {

            if (it is LinearLayout) {
                it.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                val drawable = GradientDrawable()
                drawable.setColor(getColor(it.context, R.color.dark_alpha_20))
                drawable.setSize(3, 1)
                it.dividerDrawable = drawable
            }
        }
    }
}