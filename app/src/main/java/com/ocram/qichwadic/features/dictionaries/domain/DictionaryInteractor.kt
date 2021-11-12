package com.ocram.qichwadic.features.dictionaries.domain

import android.util.Log
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import kotlinx.coroutines.flow.Flow
import java.net.UnknownHostException
import kotlin.Exception

interface DictionaryInteractor {
    fun getDictionaries(): Flow<List<DictionaryModel>>
    suspend fun saveDefinitions(dictionaryModel: DictionaryModel)
    suspend fun removeDictionary(id: Int): Boolean
    suspend fun refreshCloudDictionaries()
}
class DictionaryInteractorImpl(private val dictionaryRepository: DictionaryRepository)
    : DictionaryInteractor {
    override fun getDictionaries(): Flow<List<DictionaryModel>> {
        return dictionaryRepository.getSavedDictionaries()
    }

    override suspend fun saveDefinitions(dictionaryModel: DictionaryModel) {
        val definitions = getAllDefinitionsByDictionary(dictionaryModel.id)
        dictionaryRepository.saveDefinitions(definitions)
    }

    override suspend fun refreshCloudDictionaries() {
        try {
            val dictionaries = dictionaryRepository.getCloudDictionaries()
            if (dictionaries.isNotEmpty()) {
                dictionaryRepository.saveNewDictionaries(dictionaries)
            }
        } catch (e: Exception) {
            val errorMsg = when(e) {
                is UnknownHostException -> "Network not available"
                else -> "An error has occurred"
            }
            Log.e("ERROR", errorMsg)
        }
    }

    override suspend fun removeDictionary(id: Int): Boolean {
        return dictionaryRepository.removeDictionary(id) > 0
    }

    private suspend fun getAllDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel> {
        return dictionaryRepository.getDefinitionsByDictionary(dictionaryId)
    }
}

