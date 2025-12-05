package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.lowerLevel

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.ShelfShare
import com.euromix.esupervisor.app.enums.StoreCheckFormat
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevelRow
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevelRow
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.invisible
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ItemStoreCheckLowerLevelBinding


class StoreCheckLowerLevelAdapter(
    private val resManager: ResourceManager,
    private val settings: Settings,
    private val onSalesViewClick: (item: StoreCheckLowerLevelRow) -> Unit,
    private val onDataChanged: (position: Int, changedData: String, value: Any) -> Unit
) : ListAdapter<StoreCheckLowerLevelRow, StoreCheckLowerLevelAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemStoreCheckLowerLevelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemViewHolder(val binding: ItemStoreCheckLowerLevelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val textWatchers = mutableMapOf<EditText, TextWatcher>()

        fun bind(position: Int) {
            removeListeners()
            renderItem(position)
            setListeners(position)
        }

        private fun removeListeners() {
            textWatchers.forEach { (editText, watcher) ->
                editText.removeTextChangedListener(watcher)
            }
            textWatchers.clear()
        }

        private fun renderItem(position: Int) {
            val item = getItem(position)

            with(binding) {
                Glide.with(root.context)
                    .load(item.imgUrl)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(ivItemLogo)
                tvLowerLevel.text = item.name

                tvLowerLevel.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0,
                    if (item.sales.isNotEmpty()) R.drawable.ic_store_check_sale else 0,
                    0
                )

                if (settings.storeCheckFormat == StoreCheckFormat.YES_NO) {
                    grFormatYesNo.visible()
                    tiAmountSKU.gone()
                } else {
                    grFormatYesNo.gone()
                    tiAmountSKU.visible()

                    val etAmountSKUText = item.accountingInt.toText()
                    etAmountSKU.setText(etAmountSKUText)
                    etAmountSKU.setSelection(etAmountSKUText.length)

                    tvAmountSKUBasis.text = item.accountingIntBasis.toText("0")
                    tvAmountSKUBasis.visibility(settings.isBasis)
                }

                renderYESNOGroup(item.accountingBoolean, item.accountingBooleanBasis)

                val etShelfLengthText = item.shelfLength.toText()
                etShelfLength.setText(etShelfLengthText)
                etShelfLength.setSelection(etShelfLengthText.length)

                tvShelfLengthBasis.text = item.shelfLengthBasis.toText("0")
                tvShelfLengthBasis.visibility(settings.isBasis)

                val etShelfLengthTotalText = item.shelfLengthTotal.toText()
                etShelfLengthTotal.setText(etShelfLengthTotalText)
                etShelfLengthTotal.setSelection(etShelfLengthTotalText.length)

                tvShelfLengthTotalBasis.text = item.shelfLengthTotalBasis.toText("0")
                tvShelfLengthTotalBasis.visibility(settings.isBasis)

                grShelfShareCm.visibility(settings.shelfShare == ShelfShare.CM)


                val etFacingAmountText = item.amountFacing.toText()
                etFacingAmount.setText(etFacingAmountText)
                etFacingAmount.setSelection(etFacingAmountText.length)

                tvFacingAmountBasis.text = item.amountFacingBasis.toText("0")
                tvFacingAmountBasis.visibility(settings.isBasis)

                val etFacingAmountTotalText = item.amountFacingTotal.toText()
                etFacingAmountTotal.setText(etFacingAmountTotalText)
                etFacingAmountTotal.setSelection(etFacingAmountTotalText.length)

                tvFacingAmountTotalBasis.text = item.amountFacingTotalBasis.toText("0")
                tvFacingAmountTotalBasis.visibility(settings.isBasis)

                grFacingAmount.visibility(settings.shelfShare == ShelfShare.FACE)
                tiNewProduct.visibility(settings.newProducts)

                val etNewProductText = item.newProducts.toText()
                etNewProduct.setText(etNewProductText)
                etNewProduct.setSelection(etNewProductText.length)

                tvNewProductBasis.text = item.newProductsBasis.toText("0")
                tvNewProductBasis.visibility(settings.isBasis)

                tvInStock.isEnabled = !settings.isCheckOut
                tvNotAvailable.isEnabled = !settings.isCheckOut
                etAmountSKU.isEnabled = !settings.isCheckOut
                etShelfLength.isEnabled = !settings.isCheckOut
                etShelfLengthTotal.isEnabled = !settings.isCheckOut
                etFacingAmount.isEnabled = !settings.isCheckOut
                etFacingAmountTotal.isEnabled = !settings.isCheckOut
                etNewProduct.isEnabled = !settings.isCheckOut

            }
        }

        private fun renderYESNOGroup(accountingBoolean: Boolean, accountingBooleanBasis: Boolean) {
            with(binding) {
                if (accountingBoolean) {
                    tvInStock.background =
                        resManager.getDrawable(R.drawable.bg_6dp_white_border_gray)
                    tvInStock.setTextColor(resManager.getColor(R.color.blue))
                    tvNotAvailable.background = null
                    tvNotAvailable.setTextColor(resManager.getColor(R.color.gray_400))
                } else {
                    tvInStock.background = null
                    tvInStock.setTextColor(resManager.getColor(R.color.gray_400))
                    tvNotAvailable.background =
                        resManager.getDrawable(R.drawable.bg_6dp_white_border_gray)
                    tvNotAvailable.setTextColor(resManager.getColor(R.color.blue))
                }

                tvAvailabilityRepeated.text =
                    resManager.getString(if (accountingBooleanBasis) R.string.in_stock else R.string.not_available)
                tvAvailabilityRepeated.visibility(settings.isBasis && settings.storeCheckFormat == StoreCheckFormat.YES_NO)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setListeners(position: Int) {
            with(binding) {
                tvInStock.setOnClickListener {
                    onDataChanged(
                        position,
                        StoreCheckLowerLevelRow.ACCOUNTING_BOOLEAN,
                        true
                    )
                    renderYESNOGroup(true, getItem(position).accountingBooleanBasis)
                }

                tvNotAvailable.setOnClickListener {
                    onDataChanged(
                        position,
                        StoreCheckLowerLevelRow.ACCOUNTING_BOOLEAN,
                        false
                    )
                    renderYESNOGroup(false, getItem(position).accountingBooleanBasis)
                }

                tvLowerLevel.setOnTouchListener { v, event ->

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val textLocation = IntArray(2)
                            v.getLocationOnScreen(textLocation)

                            if (event.rawX >= textLocation[0] + tvLowerLevel.width - tvLowerLevel.totalPaddingRight) {
                                // Right drawable was tapped
                                onSalesViewClick(getItem(position))
                                return@setOnTouchListener true
                            }
                        }
                    }
                    return@setOnTouchListener false
                }

//                setOnFocusChangeListener(etAmountSKU, StoreCheckLowerLevelRow.ACCOUNTING_INT, position)
//                setOnFocusChangeListener(etShelfLength, StoreCheckLowerLevelRow.SHELF_LENGTH, position)
//                setOnFocusChangeListener(etShelfLengthTotal, StoreCheckLowerLevelRow.SHELF_LENGTH_TOTAL, position)
//                setOnFocusChangeListener(etFacingAmount, StoreCheckLowerLevelRow.AMOUNT_FACING, position)
//                setOnFocusChangeListener(etFacingAmountTotal, StoreCheckLowerLevelRow.AMOUNT_FACING_TOTAL, position)
//                setOnFocusChangeListener(etNewProduct, StoreCheckLowerLevelRow.NEW_PRODUCTS, position)

                addTextWatcher(etAmountSKU, position, StoreCheckLowerLevelRow.ACCOUNTING_INT)
                addTextWatcher(etShelfLength, position, StoreCheckLowerLevelRow.SHELF_LENGTH)
                addTextWatcher(
                    etShelfLengthTotal,
                    position,
                    StoreCheckLowerLevelRow.SHELF_LENGTH_TOTAL
                )
                addTextWatcher(etFacingAmount, position, StoreCheckLowerLevelRow.AMOUNT_FACING)
                addTextWatcher(
                    etFacingAmountTotal,
                    position,
                    StoreCheckLowerLevelRow.AMOUNT_FACING_TOTAL
                )
                addTextWatcher(etNewProduct, position, StoreCheckLowerLevelRow.NEW_PRODUCTS)
            }
        }

        private fun addTextWatcher(
            editText: EditText,
            position: Int,
            changedData: String
        ) {
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    onDataChanged(
                        position,
                        changedData,
                        if (s.isNullOrEmpty()) 0 else s.toString().toInt()
                    )
                }
            }
            editText.addTextChangedListener(watcher)
            textWatchers[editText] = watcher
        }

//        private fun setOnFocusChangeListener(tv: TextView, changedData: String, position: Int){
//            tv.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
//                if (!hasFocus) {
//                    onDataChanged(
//                        position,
//                        changedData,
//                        tv.text.toString().toIntOrNull() ?: 0
//                    )
//                }
//            }
//        }
    }


    class DiffCallback : DiffUtil.ItemCallback<StoreCheckLowerLevelRow>() {
        override fun areItemsTheSame(
            oldItem: StoreCheckLowerLevelRow,
            newItem: StoreCheckLowerLevelRow
        ) =
            oldItem.rowNumber == newItem.rowNumber

        override fun areContentsTheSame(
            oldItem: StoreCheckLowerLevelRow,
            newItem: StoreCheckLowerLevelRow
        ) =
            oldItem == newItem
    }


    class Settings(
        val storeCheckFormat: StoreCheckFormat,
        val shelfShare: ShelfShare,
        val newProducts: Boolean,
        val isCheckOut: Boolean,
        val isBasis: Boolean
    )

}