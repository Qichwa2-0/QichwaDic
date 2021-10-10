package com.ocram.qichwadic.features.dictionaries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.core.ui.DictLang
import com.ocram.qichwadic.core.ui.fragment.BaseFragment
import com.ocram.qichwadic.databinding.FragmentDictionaryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DictionaryFragment : BaseFragment<FragmentDictionaryBinding>(), AdapterView.OnItemSelectedListener, DictionaryAdapter.DefinitionDownloadListener {

    private val dictionaryViewModel: DictionaryViewModel by viewModel()
    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var dictLangMap: Map<String, List<DictionaryModel>>
    private lateinit var dictLangs: List<DictLang>
    private var currentLangCode: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()

        val toolbar = view.findViewById<Toolbar>(R.id.mToolbar)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        initViews()
        dictionaryViewModel.dictionariesByLang.observe(viewLifecycleOwner, Observer { this.onDictionaryListChanged(it) })
        dictionaryViewModel.localLoading.observe(viewLifecycleOwner, Observer { this.onLocalDictionariesLoadingChanged(it) })
        dictionaryViewModel.dictionaryActionStatus.observe(viewLifecycleOwner, Observer { this.onDictionaryAction(it) })
        dictionaryViewModel.cloudError.observe(viewLifecycleOwner, Observer {  this.onCloudError(it) })
    }

    private fun initViews() {
        dictLangs = resources.getStringArray(R.array.dictLangs).map { DictLang(it) }

        setRecyclerView()
        setSpinnerAdapters()
        binding.spTargetLanguages.onItemSelectedListener = this
    }

    private fun setRecyclerView() {
        dictionaryAdapter = DictionaryAdapter(ArrayList(), this)
        val linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvDictionaries.layoutManager = linearLayoutManager
        binding.rvDictionaries.isNestedScrollingEnabled = false
        binding.rvDictionaries.adapter = dictionaryAdapter
    }

    private fun setSpinnerAdapters() {
        val langAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner_dict_lang_white, dictLangs)
        langAdapter.setDropDownViewResource(R.layout.item_spinner_dict_lang_white)
        binding.spTargetLanguages.adapter = langAdapter
        binding.spTargetLanguages.setSelection(0)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        val dictLang = binding.spTargetLanguages.getItemAtPosition(pos) as DictLang
        this.currentLangCode = dictLang.code
        dictionaryAdapter.dictionaries = dictLangMap[dictLang.code]
        dictionaryAdapter.notifyDataSetChanged()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun downloadDefinitions(pos: Int, dictionary: DictionaryModel) {
        dictionaryViewModel.downloadDictionary(pos, dictionary)
    }

    override fun removeDictionary(pos: Int, dictionary: DictionaryModel) {
        dictionaryViewModel.removeDictionary(pos, dictionary)
    }

    private fun onDictionaryListChanged(dictionariesMap: Map<String, List<DictionaryModel>>) {
        this.dictLangMap = dictionariesMap
        if (dictionariesMap.isNotEmpty()) {
            showDictionaries(dictionariesMap[this.currentLangCode])
        }
    }

    private fun showDictionaries(dictionaries: List<DictionaryModel>?) {
        dictionaryAdapter.dictionaries = dictionaries
        dictionaryAdapter.notifyDataSetChanged()
        binding.spTargetLanguages.visibility = View.VISIBLE
        binding.rvDictionaries.visibility = View.VISIBLE
    }

    private fun onLocalDictionariesLoadingChanged(isLoading: Boolean?) {
        if (isLoading != null) {
            binding.pbDictionariesLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun onDictionaryAction(dictionaryActionState: DictionaryActionState) {

        val messageTemplate: Int?

        if(dictionaryActionState.error) {
            messageTemplate =
                    if(dictionaryActionState.dictionary.existsInLocal)
                        R.string.dictionary_delete_error
                    else
                        R.string.dictionary_save_error
        } else {
            messageTemplate =
                    if(dictionaryActionState.dictionary.existsInLocal)
                        R.string.dictionary_save_success
                    else
                        R.string.dictionary_delete_success
        }
        showMessage(getString(messageTemplate, dictionaryActionState.dictionary.name))
    }

    private fun onCloudError(hasError: Boolean) {
        if(hasError) {
            showMessage(getString(R.string.error_no_cloud_dictionaries))
        }
    }

    private fun showMessage(message: String?) {
        if (!message.isNullOrEmpty()) {
            Snackbar.make(binding.clDictionaries, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun viewBindingClass(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDictionaryBinding {
        return FragmentDictionaryBinding.inflate(inflater, container, false)
    }
}
