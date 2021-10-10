package com.ocram.qichwadic.features.help.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ocram.qichwadic.core.ui.activity.MainActivity
import com.ocram.qichwadic.core.ui.fragment.BaseFragment
import com.ocram.qichwadic.databinding.FragmentSearchHelpWritingBinding

class SearchHelpWritingFragment : BaseFragment<FragmentSearchHelpWritingBinding>() {
    override fun viewBindingClass(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSearchHelpWritingBinding {
        return FragmentSearchHelpWritingBinding.inflate(inflater, container, false)
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
