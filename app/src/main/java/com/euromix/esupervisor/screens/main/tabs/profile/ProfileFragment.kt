package com.euromix.esupervisor.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.BuildConfig
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Language
import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.FragmentProfileAndSettingsBinding
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile_and_settings) {

    private val binding by viewBinding<FragmentProfileAndSettingsBinding>()

    override val viewModel by viewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogout.setOnClickListener { logout() }
        binding.tvVersion.text = BuildConfig.VERSION_NAME
        setupObservers()
        setCheckedRBtn()
        setLanguageListeners()
    }

    private fun setupObservers() {

        viewModel.account.observe(viewLifecycleOwner) { account ->
            if (account == null) return@observe
            binding.tvLogin.text = account
        }

        viewModel.role.observe(viewLifecycleOwner) {

            binding.tvRole.text = Role.stringRepresentation(requireContext(), it)

        }
    }

    private fun setCheckedRBtn() {
        when (Lingver.getInstance().getLanguage()) {
            Language.ENG.locale().language -> binding.radioGroup.check(R.id.rbtnEn)
            Language.UA.locale().language -> binding.radioGroup.check(R.id.rbtnUa)
            else -> binding.radioGroup.check(R.id.rbtnEn)
        }
    }

    private fun setLanguageListeners() {
        binding.rbtnEn.setOnClickListener { setLanguage(Locale.ENGLISH.language) }
        binding.rbtnUa.setOnClickListener { setLanguage(Language.UA.locale().language) }

    }

    private fun setLanguage(lang: String) {

        val currentLang = Lingver.getInstance().getLanguage()
        if (currentLang != lang) {
            Lingver.getInstance().setLocale(requireContext(), lang)
            reloadStringResourcesOnScreen()
        }
    }

    private fun reloadStringResourcesOnScreen() {

        with(binding) {
            tvUserLabel.text = getString(R.string.name)
            tvLogout.text = getString(R.string.logout)
            tvChangeLanguageLabel.text = getString(R.string.change_language)
            rbtnUa.text = getString(R.string.lang_ua)
            rbtnEn.text = getString(R.string.lang_eng)
            tvVersionLabel.text = getString(R.string.version)
            tvRoleLabel.text = getString(R.string.role)
            tvRole.text = Role.stringRepresentation(requireContext(), viewModel.getCurrentRole())

        }
    }
}