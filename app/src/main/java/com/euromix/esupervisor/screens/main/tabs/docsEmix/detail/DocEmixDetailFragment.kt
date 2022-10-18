package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.RowTradeCondition
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.*
import com.euromix.esupervisor.databinding.DialogReasonRejectionBinding
import com.euromix.esupervisor.databinding.DocEmixDetailFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator

class DocEmixDetailFragment : BaseFragment(R.layout.doc_emix_detail_fragment) {

    override val viewModel by viewModelCreator { DocEmixDetailViewModel(args.extId) }

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

//            val texDate = docEmixDetail.date.toLocalDate()
//                .toFormatString(context.getString(R.string.day_month_year_date_format))

            binding.iDocEmixDetailCard.tvDate.text =
                with(docEmixDetail.date) { textDate(this, context) }

            binding.iDocEmixDetailCard.tvNumber.text = docEmixDetail.number
            binding.iDocEmixDetailCard.tvPartner.text = docEmixDetail.partner
            binding.iDocEmixDetailCard.tvStatus.text = docEmixDetail.status
            binding.iDocEmixDetailCard.tvStatus.setColorStatus(docEmixDetail.status)
            binding.iDocEmixDetailCard.tvDescription.text = docEmixDetail.description
            binding.iDocEmixDetailCard.tvOperationType.text = docEmixDetail.operationType
            binding.iDocEmixDetailCard.tvTradingAgent.text = docEmixDetail.tradingAgent

            if (docEmixDetail.canBeAgreed) {
                binding.iDocEmixDetailCard.btnApprove.visible()
                binding.iDocEmixDetailCard.btnReject.visible()
            } else {
                binding.iDocEmixDetailCard.btnApprove.gone()
                binding.iDocEmixDetailCard.btnReject.gone()
            }
            docEmixDetail.rowTradeConditions?.let { insertNestedFragment(it) }
        }

    }

    private fun insertNestedFragment(rows: List<RowTradeCondition>) {

        val childFragment = DocEmixDetailTradeConditionFragment.newInstance(rows)
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