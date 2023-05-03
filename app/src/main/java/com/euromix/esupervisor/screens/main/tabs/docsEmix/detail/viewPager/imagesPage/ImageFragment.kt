package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.databinding.ImageFragmentBinding

class ImageFragment: Fragment(R.layout.image_fragment) {

    private lateinit var binding: ImageFragmentBinding

    private val navArgs by navArgs<ImageFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ImageFragmentBinding.bind(view)

        Glide.with(view).load(navArgs.imageUri).into(binding.iv)

        binding.ivClose.setOnClickListener { findNavController().popBackStack() }

    }

}