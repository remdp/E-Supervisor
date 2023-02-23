package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.*
import com.euromix.esupervisor.databinding.DialogReasonRejectionBinding
import com.euromix.esupervisor.databinding.DocEmixDetailFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.VPFragmentAdapter
import com.euromix.esupervisor.screens.viewModelCreator
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocEmixDetailFragment : BaseFragment(R.layout.doc_emix_detail_fragment) {

    @Inject
    lateinit var factory: DocEmixDetailViewModel.Factory

    override val viewModel by viewModelCreator { factory.create(args.extId) }

    private lateinit var binding: DocEmixDetailFragmentBinding
    private val args by navArgs<DocEmixDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DocEmixDetailFragmentBinding.bind(view)
        val context = view.context

        binding.iDocEmixDetailCard.btnApprove.setOnClickListener { viewModel.acceptDocEmixDetail() }
        binding.iDocEmixDetailCard.btnReject.setOnClickListener { showInputReasonDialog() }

        binding.vResult.setTryAgainAction { viewModel.reload() }

        viewModel.docEmixDetail.observeResults(this, view, binding.vResult) { docEmixDetail ->

            with(binding) {
                iDocEmixDetailCard.tvDate.text =
                    with(docEmixDetail.date) { textDate(this, context) }

                iDocEmixDetailCard.tvNumber.text = docEmixDetail.number
                iDocEmixDetailCard.tvPartner.text = docEmixDetail.partner
                iDocEmixDetailCard.tvStatus.setNonStandartStatusText(docEmixDetail.status)
                iDocEmixDetailCard.tvStatus.setColorStatus(docEmixDetail.status)
                iDocEmixDetailCard.tvDescription.text = docEmixDetail.description
                iDocEmixDetailCard.tvOperationType.text =
                    getString(docEmixDetail.operationType.nameStringRes())
                iDocEmixDetailCard.tvTradingAgent.text = docEmixDetail.tradingAgent

                if (docEmixDetail.canBeAgreed) {
                    iDocEmixDetailCard.btnApprove.visible()
                    iDocEmixDetailCard.btnReject.visible()
                } else {
                    iDocEmixDetailCard.btnApprove.gone()
                    iDocEmixDetailCard.btnReject.gone()
                }

                if (docEmixDetail.operationType == DocEmixOperationType.ReturnRequest) {
                    iDocEmixDetailCard.tvSum.text = docEmixDetail.sum.toString()
                    iDocEmixDetailCard.tvSumLabel.visible()
                    iDocEmixDetailCard.tvSum.visible()
                }

                setupViewPager(docEmixDetail)
            }
        }
    }

    private fun setupViewPager(docEmixDetail: DocEmixDetail) {

        with(binding) {
            vpTabs.adapter = VPFragmentAdapter(this@DocEmixDetailFragment, docEmixDetail)

            if (docEmixDetail.operationType == DocEmixOperationType.NewPartnerFact || (docEmixDetail.picturesPaths?.size
                    ?: 0) > 0
            ) {
                TabLayoutMediator(tlTabs, vpTabs) { tab, position ->

                    when (docEmixDetail.operationType) {

                        DocEmixOperationType.ChangeTC -> {

                            tab.text = when (position) {
                                CHANGE_TC_PAGE -> getString(R.string.trade_condition)
                                else -> getString(
                                    R.string.picture_number,
                                    position + 1 - VPFragmentAdapter.CHANGE_TC_FRAGMENTS_COUNT
                                )
                            }
                        }
                        DocEmixOperationType.NewPartnerFact -> {

                            tab.text = when (position) {
                                NEW_PARTNER_PAGE -> getString(R.string.new_partner_data)
                                NEW_OUTLET_PAGE -> getString(R.string.new_outlet_data)
                                TC_PAGE -> getString(R.string.tc_data)
                                else -> getString(
                                    R.string.picture_number,
                                    position + 1 - VPFragmentAdapter.NEW_PARTNER_FACT_FRAGMENTS_COUNT
                                )

                            }
                        }
                        DocEmixOperationType.ReturnRequest -> {

                            tab.text = when (position) {
                                RETURN_REQUEST_PAGE -> getString(R.string.goods)
                                else -> getString(
                                    R.string.picture_number,
                                    position + 1 - VPFragmentAdapter.RETURN_REQUEST_FRAGMENTS_COUNT
                                )
                            }
                        }
                        else -> {}
                    }
                }.attach()
            } else {
                tlTabs.gone()
            }
        }
    }

    private fun showInputReasonDialog() {
        val dialogBinding = DialogReasonRejectionBinding.inflate(layoutInflater)

        context?.let {
            val dialog = AlertDialog.Builder(it)
                .setTitle(R.string.reason_input)
                .setView(dialogBinding.root)
                .setPositiveButton(R.string.ok, null)
                .create()

            dialog.setOnShowListener {
                dialogBinding.tfReasonText.requestFocus()
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val enteredText = dialogBinding.tfReasonText.text.toString()

                    viewModel.rejectDocEmixDetail(enteredText)
                    dialog.dismiss()
                }
            }
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
        }
    }

    companion object {
        const val CHANGE_TC_PAGE = 0
        const val NEW_PARTNER_PAGE = 0
        const val NEW_OUTLET_PAGE = 1
        const val TC_PAGE = 2
        const val RETURN_REQUEST_PAGE = 0
    }
}