package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.picturePage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.utils.parcelable
import com.euromix.esupervisor.databinding.PhotoFragmentBinding

class DocEmixDetailPictureFragment : Fragment(R.layout.photo_fragment) {

    private lateinit var binding: PhotoFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = PhotoFragmentBinding.bind(view)

        arguments?.parcelable<String>(PATH)?.let {

            Glide.with(this).load(it).into(binding.imageView)
        }
    }

    companion object {

        fun newInstance(picturePath: String): DocEmixDetailPictureFragment =
            DocEmixDetailPictureFragment().apply {
                arguments = bundleOf(PATH to picturePath)
            }

        private const val PATH = "path"
    }
}