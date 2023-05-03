package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docEmix.entities.ImagesPaths
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.databinding.ImagesFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.DocEmixDetailFragmentDirections
import java.time.LocalDateTime

class ImagesFragment : Fragment(R.layout.images_fragment) {

    private lateinit var binding: ImagesFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ImagesFragmentBinding.bind(view)

        arguments?.let { args ->

            val adapter = ImagesAdapter { imageUri ->
                val direction =
                    DocEmixDetailFragmentDirections.actionDocEmixDetailFragmentToImageFragment(
                        imageUri = imageUri,
                        titleData = TitleData(args.getString(KEY_NUMBER), args.getString(KEY_DATE))
                    )
                findNavController().navigate(direction)
            }

            adapter.setImages(args)
            binding.rvImages.adapter = adapter

        }
    }

    companion object {

        fun newInstance(
            paths: List<ImagesPaths>,
            number: String,
            date: LocalDateTime
        ): ImagesFragment = ImagesFragment().apply {

            if (paths.isNotEmpty()) {
                val bundle = Bundle()
                for (i in 1..paths.size) {
                    bundle.putString(PATH + i, paths[i - 1].path)
                }
                bundle.putString(KEY_NUMBER, number)
                bundle.putString(KEY_DATE, textDate(date))
                arguments = bundle
            }
        }

        private const val PATH = "PATH"
        private const val KEY_NUMBER = "KEY_NUMBER"
        private const val KEY_DATE = "KEY_DATE"
    }
}