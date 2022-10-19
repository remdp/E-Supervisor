package com.euromix.esupervisor.screens.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import ua.cn.stu.navcomponent.tabs.screens.main.auth.SignInViewModel

@AndroidEntryPoint
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {

    private lateinit var binding: FragmentSignInBinding

   // private val viewModel by viewModelCreator { SignInViewModel() }
    override val viewModel by viewModels<SignInViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInBinding.bind(view)
        binding.btnLogin.setOnClickListener { onSignInButtonPressed() }

        observeState()
        observeClearPasswordEvent()
        observeShowAuthErrorMessageEvent()
        observeNavigateToTabsEvent()
    }

    private fun onSignInButtonPressed() {
        viewModel.signIn(
            userName = binding.etLogin.text.toString(),
            password = binding.etPassword.text.toString()
        )
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.etLogin.error = if (it.emptyUserNameError) getString(R.string.field_is_empty) else null
        binding.etPassword.error = if (it.emptyPasswordError) getString(R.string.field_is_empty) else null

        binding.etLogin.isEnabled = it.enableViews
        binding.etPassword.isEnabled = it.enableViews
        binding.btnLogin.isEnabled = it.enableViews
        binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.INVISIBLE
    }

    private fun observeShowAuthErrorMessageEvent() = viewModel.showAuthToastEvent.observeEvent(viewLifecycleOwner) {
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
    }

    private fun observeClearPasswordEvent() = viewModel.clearPasswordEvent.observeEvent(viewLifecycleOwner) {
        binding.etPassword.text?.clear()
    }

    private fun observeNavigateToTabsEvent() = viewModel.navigateToTabsEvent.observeEvent(viewLifecycleOwner) {
        findNavController().navigate(R.id.action_signInFragment_to_tabsFragment)

    }

}