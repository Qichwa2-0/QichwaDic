package com.ocram.qichwadic.features.help.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ocram.qichwadic.core.ui.activity.MainActivity
import com.ocram.qichwadic.databinding.FragmentSearchHelpWritingBinding

class SearchHelpWritingFragment : Fragment() {

    private var _binding: FragmentSearchHelpWritingBinding? = null;
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchHelpWritingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ytUrl = "https://www.youtube.com/watch?v=wgCq5X3ezKo&list=PLYJ5xaOE7Z-kmctE2OICkvVH_GFDEj_zP"
        listOf(binding.ivQillqayVideo, binding.btnWatchQillqayVideo).forEach {
            it.setOnClickListener {
                (requireActivity() as MainActivity).openActionViewIntent(ytUrl)
            }
        }
    }
}
