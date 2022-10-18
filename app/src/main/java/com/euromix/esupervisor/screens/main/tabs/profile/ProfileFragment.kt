package com.euromix.esupervisor.screens.main.tabs.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Language
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.databinding.FragmentProfileAndSettingsBinding
import com.euromix.esupervisor.screens.viewModelCreator
import com.yariksoffice.lingver.Lingver
import java.util.*

class ProfileFragment : BaseFragment(R.layout.fragment_profile_and_settings) {

    private lateinit var binding: FragmentProfileAndSettingsBinding

    override val viewModel by viewModelCreator { ProfileViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileAndSettingsBinding.bind(view)

        binding.btnLogout.setOnClickListener { logout() }
        observeAccountDetails()
        setLanguage()
    }

    private fun observeAccountDetails() {
        viewModel.account.observe(viewLifecycleOwner) { account ->
            if (account == null) return@observe
            binding.tvLogin.text = account
        }
    }

    private fun setLanguage() {
        when (Lingver.getInstance().getLanguage()) {
            Language.ENG.locale().language -> binding.tvLanguage.setText(Language.ENG.nameStringRes())
            Language.UA.locale().language -> binding.tvLanguage.setText(Language.UA.nameStringRes())
            else -> binding.tvLanguage.setText(Language.UNDEFINED.nameStringRes())
        }
        binding.tvLanguage.setOnClickListener {

            val titles = Language.values().associateBy { language ->
                getString(language.nameStringRes())
            }

            val popup = PopupMenu(requireContext(), binding.tvLanguage)
            titles.forEach { popup.menu.add(it.key) }

            popup.setOnMenuItemClickListener { selectedTitle ->
                val selectedLanguage: Language =
                    titles[selectedTitle.toString()] ?: Language.UNDEFINED
                if (Lingver.getInstance().getLanguage() != selectedLanguage.locale().language) {
                    Lingver.getInstance().setLocale(
                        requireContext(),
                        selectedLanguage.locale().language ?: Locale.ENGLISH.language
                    )
                    binding.tvLanguage.text = getString(selectedLanguage.nameStringRes())
                    reloadStringResourcesOnScreen()
                }
                true
            }
            popup.show()
        }
    }

    private fun reloadStringResourcesOnScreen() {

        with(binding) {
            tvAppSettings.text = getString(R.string.app_settings)
            tvAppLanguage.text = getString(R.string.app_language)
            tvAccount.text = getString(R.string.account)
            btnLogout.text = getString(R.string.logout)
        }
    }


}