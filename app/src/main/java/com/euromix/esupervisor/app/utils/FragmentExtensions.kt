package com.euromix.esupervisor.app.utils

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.views.ResultView

fun Fragment.findTopNavController(): NavController {
    val topLevelHost =
        requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment?
    return topLevelHost?.navController ?: findNavController()
}

fun Fragment.designByResult(
    result: Result<*>,
    root: View,
    resultView: ResultView,
    srl: SwipeRefreshLayout? = null,
    specialView: View? = null
) {

    val rootView: View = if (root is ScrollView)
        root.getChildAt(0)
    else
        root

    if (rootView is ViewGroup && rootView !is RecyclerView && root !is AbsListView) {
        rootView.children
            .filter { it != resultView }
            .forEach {

                if (result is Error) it.isVisible = false
                else {
                    if (it != specialView)
                        it.isVisible = true
                }
                //it.isVisible = result !is Error<*>
            }
    }

    srl?.let { it.isRefreshing = result is Pending }
    resultView.setResult(this as BaseFragment, result, srl == null)

}
