package com.ocram.qichwadic.features.dictionaries.domain

import android.util.Log
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import kotlin.Exception

interface DictionaryInteractor {
    suspend fun getDictionaries(): Flow<List<DictionaryModel>>
    suspend fun saveDefinitions(dictionaryModel: DictionaryModel)
    suspend fun removeDictionary(id: Int): Boolean
    suspend fun refreshCloudDictionaries()
}
class DictionaryInteractorImpl(
    private val dictionaryRepository: DictionaryRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
    )
    : DictionaryInteractor {
    override suspend fun getDictionaries(): Flow<List<DictionaryModel>> {
        return withContext(defaultDispatcher) {
            dictionaryRepository.getSavedDictionaries()
        }
    }

    override suspend fun saveDefinitions(dictionaryModel: DictionaryModel) {
        val definitions = getAllDefinitionsByDictionary(dictionaryModel.id)
        dictionaryRepository.saveDefinitions(definitions)
    }

    override suspend fun refreshCloudDictionaries() {
        try {
            withContext(defaultDispatcher) {
                val dictionaries = dictionaryRepository.getCloudDictionaries()
                if (dictionaries.isNotEmpty()) {
                    dictionaryRepository.saveNewDictionaries(dictionaries)
                }
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

