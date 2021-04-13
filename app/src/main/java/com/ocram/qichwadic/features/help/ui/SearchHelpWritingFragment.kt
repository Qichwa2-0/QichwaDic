package com.ocram.qichwadic.features.help.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_search_help_writing.*

class SearchHelpWritingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_help_writing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ytUrl = "https://www.youtube.com/watch?v=wgCq5X3ezKo&list=PLYJ5xaOE7Z-kmctE2OICkvVH_GFDEj_zP"
        listOf(ivQillqayVideo, btnWatchQillqayVideo).forEach {
            it.setOnClickListener {
                (requireActivity() as MainActivity).openActionViewIntent(ytUrl)
            }
        }
    }

}
