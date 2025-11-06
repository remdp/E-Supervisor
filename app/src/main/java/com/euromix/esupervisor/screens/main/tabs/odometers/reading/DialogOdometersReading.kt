package com.euromix.esupervisor.screens.main.tabs.odometers.reading

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.common.Action
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.base64StringFromUri
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.isTimeZero
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setBitmapFromBase64String
import com.euromix.esupervisor.app.utils.toTextHMS
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.OdometerReadingFragmentBinding
import com.euromix.esupervisor.dialogs.selectPictureDialog.SelectPictureDialog
import com.euromix.esupervisor.dialogs.selectPictureDialog.SelectPictureViewModel
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.graphics.drawable.toDrawable

@AndroidEntryPoint
class DialogOdometersReading(private val listUpdater: Action) : DialogFragment() {

    private lateinit var binding: OdometerReadingFragmentBinding

    private val selectPictureViewModel by activityViewModels<SelectPictureViewModel>()

    private val viewModel by viewModels<DialogOdometersViewModel>()

    @Inject
    lateinit var resManager: ResourceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = OdometerReadingFragmentBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val dialogView =
            dialog?.window?.decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val params = dialogView.layoutParams as FrameLayout.LayoutParams


        val margin = resources.getDimensionPixelSize(R.dimen.DP_8)
        params.setMargins(margin, 0, margin, 0)
        dialogView.layoutParams = params

    }

    override fun onDestroy() {
        super.onDestroy()
        listUpdater()
    }

    private fun setupListeners() {

        with(binding) {

            binding.vResult.setTryAgainAction { viewModel.reload() }
            btnCancel.setOnClickListener { dismiss() }

            btnSend.setOnClickListener {
                (if (!viewModel.viewState.isStart) viewModel.viewState.startUri else viewModel.viewState.stopUri)?.let { uri ->
                    viewModel.sendTodayOdometersReading(
                        base64StringFromUri(requireContext(), uri)
                    )
                }
            }

            etStartOdometer.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {

                    s?.let {
                        it.toString().also { startKm ->
                            val startKmEdit = if (startKm.isNotEmpty()) startKm.toInt() else null

                            if (startKmEdit != viewModel.viewState.startKm)
                                viewModel.setStartKm(startKmEdit)
                        }
                    }
                }
            })

            etStopOdometer.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {

                    s?.let {
                        it.toString().also { stopKm ->
                            val stopKmKmEdit = if (stopKm.isNotEmpty()) stopKm.toInt() else null

                            if (stopKmKmEdit != viewModel.viewState.stopKm)
                                viewModel.setStopKm(stopKmKmEdit)
                        }
                    }
                }
            })
        }

        binding.clStartOdometerPhoto.setOnClickListener {
            if (!viewModel.viewState.isStart)
                SelectPictureDialog.newInstance().show(parentFragmentManager, null)
        }

        binding.clStopOdometerPhoto.setOnClickListener {
            if (!viewModel.viewState.isStop)
                SelectPictureDialog.newInstance().show(parentFragmentManager, null)
        }
    }

    private fun setupObservers() {

        selectPictureViewModel.uriEvent.observeEvent(viewLifecycleOwner) {
            viewModel.selectPicture(it)
        }

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun renderState() {

        with(viewModel.viewState) {
            designByViewState(
                this as BaseViewState, binding.root, binding.vResult
            )

            if (error == null) {
                setVisibility()

                if (!isLoading)
                    setViews()
            }
        }
    }

    private fun setViews() {
        with(viewModel.viewState) {
            with(binding) {

                tvCarNumber.text = carNUmber

                val startKmString = (startKm ?: "").toString()
                val stopKmString = (stopKm ?: "").toString()

                if (etStartOdometer.text.toString() != startKmString)
                    etStartOdometer.setText(startKmString)

                if (etStopOdometer.text.toString() != stopKmString)
                    etStopOdometer.setText(stopKmString)

                tvOdometerDifference.text = if ((stopKm ?: 0) > 0) {
                    ((stopKm ?: 0) - (startKm ?: 0)).toString()
                } else {
                    "0"
                }

                if (startUri != null) {
                    Glide.with(ivOdometerPhotoStart)
                        .load(startUri)
                        .into(ivOdometerPhotoStart)
                } else
                    ivOdometerPhotoStart.setBitmapFromBase64String(startPhoto)

                if (stopUri != null) {
                    Glide.with(ivOdometerPhotoStop)
                        .load(stopUri)
                        .into(ivOdometerPhotoStop)
                } else
                    ivOdometerPhotoStop.setBitmapFromBase64String(stopPhoto)

                binding.tiStartOdometer.background =
                    resManager.getDrawable(if (isStart) R.drawable.bg_8dp_dark_alpha_10 else R.drawable.bg_8dp_white)
                binding.tiStopOdometer.background =
                    resManager.getDrawable(if (isStop) R.drawable.bg_8dp_dark_alpha_10 else R.drawable.bg_8dp_white)

                startTime?.let {
                    if (!it.isTimeZero())
                        binding.tvStartTime.text = it.toTextHMS()
                }
                stopTime?.let {
                    if (!it.isTimeZero())
                        binding.tvStopTime.text = it.toTextHMS()
                }

                btnCancel.text =
                    getString(if (startPhoto != null && stopPhoto != null) R.string.close else R.string.cancel)
            }
        }
    }

    private fun setVisibility() {

        with(viewModel.viewState) {

            binding.tiStartOdometer.visibility(!isLoading, false)
            binding.tiStartOdometer.isEnabled = !isStart

            binding.tiStopOdometer.visibility(isStart && !isLoading)
            binding.tiStopOdometer.isEnabled = !isStop

            binding.grDifference.visibility(startKm != null && stopKm != null && !isLoading)

            binding.clStartOdometerPhoto.visibility(!isLoading, false)
            binding.clStopOdometerPhoto.visibility(isStart && !isLoading)

            binding.btnSend.isEnabled =
                (!isStart && startUri != null && startKm != null) || (!viewModel.viewState.isStop && stopUri != null && stopKm != null)

            if (startPhoto != null)
                binding.ivStartPhotoStatus.setImageResource(R.drawable.ic_check_green_round)
            else if (startUri != null)
                binding.ivStartPhotoStatus.setImageResource(R.drawable.ic_clock_orange)
            else
                binding.ivStartPhotoStatus.setImageDrawable(null)

            if (stopPhoto != null)
                binding.ivStopPhotoStatus.setImageResource(R.drawable.ic_check_green_round)
            else if (stopUri != null)
                binding.ivStopPhotoStatus.setImageResource(R.drawable.ic_clock_orange)
            else
                binding.ivStopPhotoStatus.setImageDrawable(null)

            binding.tvStartTimeLabel.visibility(startTime != null && !startTime.isTimeZero() && !isLoading)
            binding.tvStartTime.visibility(!isLoading)
            binding.tvStopTimeLabel.visibility(stopTime != null && !stopTime.isTimeZero())

            binding.clAppbarBottom.visibility(!isLoading, false)
        }
    }

    companion object {
        fun newInstance(listUpdater: Action) = DialogOdometersReading(listUpdater)

    }
}