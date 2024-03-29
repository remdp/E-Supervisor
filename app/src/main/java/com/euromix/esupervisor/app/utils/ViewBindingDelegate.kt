package com.euromix.esupervisor.app.utils

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.IllegalStateException
import kotlin.reflect.KProperty

inline fun <reified B : ViewBinding> Fragment.viewBinding(): ViewBindingDelegate<B> =
    ViewBindingDelegate(this, B::class.java)

inline fun <reified B : ViewBinding> DialogFragment.viewBinding(): ViewBindingDelegate<B> =
    ViewBindingDelegate(this as Fragment, B::class.java)

class ViewBindingDelegate<B : ViewBinding>(
    private val fragment: Fragment,
    private val viewBindingClass: Class<B>
) {

    private var binding: B? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): B {
        val viewLifecycleOwner = fragment.viewLifecycleOwner

        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            throw IllegalStateException("Called after onDestroyView()")
        } else if (fragment.view != null) {
            return getOrCreateBinding(viewLifecycleOwner)
        } else throw IllegalStateException("Called before onViewCreated()")
    }

    @Suppress("UNCHECKED_CAST")
    private fun getOrCreateBinding(viewLifecycleOwner: LifecycleOwner): B {
        return binding ?: let {
            val method = viewBindingClass.getMethod("bind", View::class.java)
            val binding = method.invoke(null, fragment.view) as B

            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    this@ViewBindingDelegate.binding = null
                }
            })
            this.binding = binding
            binding
        }
    }

}