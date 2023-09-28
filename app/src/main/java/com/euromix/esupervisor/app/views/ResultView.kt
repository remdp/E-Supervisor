package com.euromix.esupervisor.app.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.AuthException
import com.euromix.esupervisor.app.model.ConnectionException
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.gone
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

    fun <T> setResult(fragment: Fragment, result: Result<T>, showPB: Boolean) {

        binding.messageTextView.isVisible = result is Error<*>
        binding.errorButton.isVisible = result is Error<*>
        binding.progressBar.isVisible = result is Pending<*> && showPB
        if (result is Error) {
            val message = when (result.error) {
                is ConnectionException -> context.getString(R.string.connection_error)
                is AuthException -> context.getString(R.string.auth_error)
                else -> result.error.message
            }
            binding.messageTextView.text = message
            if (result.error is AuthException) {
                if (fragment is BaseFragment)
                    fragment.logout()
            } else {
                renderTryAgainButton()
            }
        }
    }

    private fun renderTryAgainButton() {
        if (tryAgainAction == null) binding.errorButton.gone()
        else {
            binding.errorButton.setOnClickListener { tryAgainAction?.invoke() }
            binding.errorButton.setText(R.string.action_try_again)
        }
    }
}