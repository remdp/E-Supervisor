package com.euromix.esupervisor.app.utils

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import com.euromix.esupervisor.App.Companion.getString
import com.euromix.esupervisor.R
import com.euromix.esupervisor.databinding.DialogReactDislikeBinding
import com.euromix.esupervisor.databinding.DialogReasonRejectionBinding
import java.util.Calendar
import java.util.Date

//todo maybe split this dialog
fun dialogPositiveButton(
    context: Context,
    titleId: Int,
    addTextId: Int? = null,
    fm: FragmentManager? = null,
    onClickListener: (text: String, additionalFlag: Boolean, date: Date?) -> Unit
) {
    val dialogBinding = DialogReasonRejectionBinding.inflate(LayoutInflater.from(context))

    var deadline: Date? = null

    if (addTextId == null) {
        dialogBinding.cbAdditional.gone()
        dialogBinding.tvDeadline.gone()
    } else {
        dialogBinding.cbAdditional.text = getString(context, addTextId)
        setDateSelection(dialogBinding.tvDeadline, fm!!) {
            deadline = it
        }
    }

    val dialog = AlertDialog.Builder(context).setTitle(titleId).setView(dialogBinding.root)
        .setPositiveButton(R.string.ok, null).create()

    dialog.setOnShowListener {
        dialogBinding.tfReasonText.requestFocus()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val enteredText = dialogBinding.tfReasonText.text.toString()
            onClickListener(enteredText, dialogBinding.cbAdditional.isChecked, deadline)
            dialog.dismiss()
        }
    }
    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    dialog.show()

}


fun simplyMessageDialog(
    context: Context, message: String, title: String
) {

    val builder = android.app.AlertDialog.Builder(context)

    val dialog = with(builder) {
        setTitle(title)
        setMessage(message)
        setPositiveButton("OK", null)
        create()
    }

    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { dialog.dismiss() }
    }
    dialog.show()
}

fun dialogErrors(
    context: Context, errors: List<Int>
) {

    var message = getString(context, R.string.need_fill_colon)

    errors.forEach {
        message = message + "\n" + getString(context, it)
    }

    val builder = android.app.AlertDialog.Builder(context)

    val dialog = with(builder) {
        setTitle(R.string.validation_errors)
        setMessage(message)
        setPositiveButton("OK", null)
        create()
    }

    dialog.setOnShowListener {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setOnClickListener { dialog.dismiss() }
    }
    dialog.show()
}


fun dialogReactDislike(
    context: Context,
    titleId: Int,
    addTextId: Int,
    fm: FragmentManager,
    abilityCreateTask: Boolean,
    onClickListener: (text: String, additionalFlag: Boolean, deadline: Date) -> Unit
) {
    val binding = DialogReactDislikeBinding.inflate(LayoutInflater.from(context))

    var deadline: Date = Calendar.getInstance().time

    binding.cbAdditional.text = getString(context, addTextId)
    setDateSelection(binding.tvDeadline, fm) {
        if (it != null) {
            deadline = it
        }
    }

    binding.tfReasonText.doOnTextChanged { text, start, before, count ->
        binding.tfReasonText.setBackgroundResource(if (text.isNullOrBlank()) R.drawable.bg_underline_red else R.drawable.bg_8dp_white_border)
    }

    val dialog = AlertDialog.Builder(context).setTitle(titleId).setView(binding.root)
        .setPositiveButton(R.string.ok, null).create()

    dialog.setOnShowListener {
        binding.tfReasonText.requestFocus()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {

            if (binding.tfReasonText.text.isNullOrBlank()) {
                dialogErrors(context, listOf(R.string.dislike_reason))
            } else {
                val enteredText = binding.tfReasonText.text.toString()
                onClickListener(enteredText, binding.cbAdditional.isChecked, deadline)
                dialog.dismiss()
            }
        }
    }

    if (abilityCreateTask){
        binding.cbAdditional.visible()
        binding.tvDeadline.visible()
    }else{
        binding.cbAdditional.gone()
        binding.tvDeadline.gone()
    }


    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    dialog.show()

}