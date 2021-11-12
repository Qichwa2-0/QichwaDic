package com.ocram.qichwadic.core.data.model

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel

fun DictionaryEntity.toDictionaryModel() = DictionaryModel(
    id,
    name,
    author,
    description,
    languageBegin,
    languageEnd,
    isQuechua,
    totalEntries
)

fun DictionaryEntity.Companion.fromDictionaryModel(dictionaryModel: DictionaryModel) = DictionaryEntity(
    dictionaryModel.id,
    dictionaryModel.name,
    dictionaryModel.author,
    dictionaryModel.description,
    dictionaryModel.languageBegin,
    dictionaryModel.languageEnd,
    dictionaryModel.isQuechua,
    dictionaryModel.totalEntries
)

fun DictionaryWithStatusEntity.toDictionaryModel(): DictionaryModel {
    return dictionaryEntity.toDictionaryModel().apply { existsInLocal = hasDefinitionsSaved }
}

fun DefinitionEntity.toDefinitionModel() = DefinitionModel(
    id,
    word,
    meaning,
    summary,
    dictionaryName,
    dictionaryId
)

fun DefinitionEntity.Companion.fromDefinitionModel(definitionModel: DefinitionModel): DefinitionEntity {
    return DefinitionEntity(
        definitionModel.id,
        definitionModel.word,
        definitionModel.meaning,
        definitionModel.summary,
        definitionModel.dictionaryName,
        definitionModel.dictionaryId
    )
}

fun FavoriteEntity.toDefinitionModel(): DefinitionModel {
    return DefinitionModel(
        id,
        word,
        meaning,
        summary,
        dictionaryName,
        dictionaryId
    )
}

fun FavoriteEntity.Companion.fromDefinitionModel(definitionModel: DefinitionModel): FavoriteEntity {
    return FavoriteEntity(
        definitionModel.id,
        definitionModel.word,
        definitionModel.meaning,
        definitionModel.summary,
        definitionModel.dictionaryName,
        definitionModel.dictionaryId
    )
}

fun SearchResultEntity.toSearchResultModel(): SearchResultModel {
    return SearchResultModel(
        dictionaryId,
        dictionaryName,
        total,
        definitions.map { it.toDefinitionModel() }.toMutableList()
    )
}