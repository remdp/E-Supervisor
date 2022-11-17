package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.*
import com.euromix.esupervisor.databinding.DialogReasonRejectionBinding
import com.euromix.esupervisor.databinding.DocEmixDetailFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.DocEmixDetailVPFragment
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocEmixDetailFragment : BaseFragment(R.layout.doc_emix_detail_fragment) {

    @Inject lateinit var factory: DocEmixDetailViewModel.Factory

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

            binding.iDocEmixDetailCard.tvDate.text =
                with(docEmixDetail.date) { textDate(this, context) }

            binding.iDocEmixDetailCard.tvNumber.text = docEmixDetail.number
            binding.iDocEmixDetailCard.tvPartner.text = docEmixDetail.partner
            //binding.iDocEmixDetailCard.tvStatus.text = docEmixDetail.status
            binding.iDocEmixDetailCard.tvStatus.setNonStandartStatusText(docEmixDetail.status)
            binding.iDocEmixDetailCard.tvStatus.setColorStatus(docEmixDetail.status)
            binding.iDocEmixDetailCard.tvDescription.text = docEmixDetail.description
            binding.iDocEmixDetailCard.tvOperationType.text = getString(docEmixDetail.operationType.nameStringRes())
            binding.iDocEmixDetailCard.tvTradingAgent.text = docEmixDetail.tradingAgent

            if (docEmixDetail.canBeAgreed) {
                binding.iDocEmixDetailCard.btnApprove.visible()
                binding.iDocEmixDetailCard.btnReject.visible()
            } else {
                binding.iDocEmixDetailCard.btnApprove.gone()
                binding.iDocEmixDetailCard.btnReject.gone()
            }
            //docEmixDetail.rowTradeConditions?.let { insertNestedFragment(it) }
            insertNestedFragment(docEmixDetail)
        }

    }

    private fun insertNestedFragment(docEmixDetail: DocEmixDetail) {

        val childFragment = DocEmixDetailVPFragment.newInstance(docEmixDetail)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fCVRowsTradingCondition, childFragment).commit()
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
}