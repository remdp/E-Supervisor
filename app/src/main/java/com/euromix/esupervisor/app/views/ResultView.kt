package com.euromix.esupervisor.app.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.databinding.ViewResultBinding

class ResultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewResultBinding
    private var tryAgainAction: (() -> Unit)? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_result, this, true)
        binding = ViewResultBinding.bind(this)
    }

    fun setTryAgainAction(action: () -> Unit) {
        this.tryAgainAction = action
    }

    fun <T> setResult(fragment: BaseFragment, result: Result<T>) {

        binding.messageTextView.isVisible = result is Error<*>
        binding.errorButton.isVisible = result is Error<*>
        binding.progressBar.isVisible = result is Pending<*>
        if (result is Error) {
            Log.e(javaClass.simpleName, "Error", result.error)
            val message = when (result.error) {
                is ConnectionException -> context.getString(R.string.connection_error)
                is AuthException -> context.getString(R.string.auth_error)
                is BackendException -> result.error.message
                else -> context.getString(R.string.internal_error)
            }
            binding.messageTextView.text = message
            if (result.error is AuthExceptionWithMessage) {
                Toast.makeText(context, result.error.message, Toast.LENGTH_LONG).show()
                fragment.logout()
            } else if (result.error is AuthException) {
//                fragment.logout()
            } else {
                renderTryAgainButton()
            }
        }
    }

    private fun renderTryAgainButton() {
        binding.errorButton.setOnClickListener { tryAgainAction?.invoke() }
        binding.errorButton.setText(R.string.action_try_again)
    }

}