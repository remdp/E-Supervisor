package com.euromix.esupervisor.app.utils

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.views.ResultView

fun <T> LiveData<T>.requireValue(): T {
    return this.value ?: throw IllegalStateException("Value is empty")
}

fun <T> LiveData<Result<T>>.observeResults(
    fragment: BaseFragment,
    root: View,
    resultView: ResultView,
    srl: SwipeRefreshLayout? = null,
    onSuccess: (T) -> Unit,
) {
    observe(fragment.viewLifecycleOwner) { result ->

        val rootView: View = if (root is ScrollView)
            root.getChildAt(0)
        else
            root

        if (rootView is ViewGroup && rootView !is RecyclerView && root !is AbsListView) {
            rootView.children
                .filter { it != resultView }
                .forEach {
                    it.isVisible = result !is Error<*>
                }
        }

        val showPBInResultView = srl !is SwipeRefreshLayout
        if (!showPBInResultView) srl!!.isRefreshing = result is Pending
        resultView.setResult(fragment, result, showPBInResultView)
        if (result is Success) onSuccess.invoke(result.value)

    }
}