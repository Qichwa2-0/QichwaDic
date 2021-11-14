package com.ocram.qichwadic.core.data.local.model

import com.ocram.qichwadic.core.data.model.*
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import org.junit.Assert
import org.junit.Test

class MapperTest {

    @Test
    fun testDictionaryEntityToModel() {
        val entity = DictionaryEntity(
            id = 1,
            name = "Dictionary",
            author = "Author",
            description = "Description",
            languageBegin = "qu",
            languageEnd = "es",
            isQuechua = true,
            totalEntries = 1000
        )
        val model = entity.toDictionaryModel()

        testEqualsDictionary(entity, model)
    }

    @Test
    fun testDictionaryModelToEntity() {
        val model = DictionaryModel(
            id = 1,
            name = "Dictionary",
            author = "Author",
            description = "Description",
            languageBegin = "qu",
            languageEnd = "es",
            isQuechua = true,
            totalEntries = 1000,
            existsInLocal = false,
            downloading = false
        )

        val entity = DictionaryEntity.fromDictionaryModel(model)

        testEqualsDictionary(entity, model)
    }

    @Test
    fun testDictionaryWithStatusEntityToModel_noDefinitions() {
        val entityWithStatusWithDefinitions = DictionaryWithStatusEntity(
            dictionaryEntity = DictionaryEntity(
                id = 1,
                name = "Dictionary",
                author = "Author",
                description = "Description",
                languageBegin = "qu",
                languageEnd = "es",
                isQuechua = true,
                totalEntries = 1000
            ),
            hasDefinitionsSaved = true
        )

        val modelWithDefinitions = entityWithStatusWithDefinitions.toDictionaryModel()

        testEqualsDictionary(
            entityWithStatusWithDefinitions.dictionaryEntity,
            modelWithDefinitions
        )
        Assert.assertEquals(
            entityWithStatusWithDefinitions.hasDefinitionsSaved,
            modelWithDefinitions.existsInLocal
        )
    }

    @Test
    fun testDictionaryWithStatusEntityToModel_withDefinitions() {
        val entityWithStatusWithoutDefinitions = DictionaryWithStatusEntity(
            dictionaryEntity = DictionaryEntity(
                id = 2,
                name = "Dictionary 2",
                author = "Author 2",
                description = "Description 2",
                languageBegin = "de",
                languageEnd = "qu",
                isQuechua = false,
                totalEntries = 400
            ),
            hasDefinitionsSaved = false
        )

        val modelWithoutDefinitions = entityWithStatusWithoutDefinitions.toDictionaryModel()

        testEqualsDictionary(
            entityWithStatusWithoutDefinitions.dictionaryEntity,
            modelWithoutDefinitions
        )
        Assert.assertEquals(
            entityWithStatusWithoutDefinitions.hasDefinitionsSaved,
            modelWithoutDefinitions.existsInLocal
        )
    }

    private fun testEqualsDictionary(entity: DictionaryEntity, model: DictionaryModel) {
        Assert.assertEquals(entity.id, model.id)
        Assert.assertEquals(entity.name, model.name)
        Assert.assertEquals(entity.author, model.author)
        Assert.assertEquals(entity.description, model.description)
        Assert.assertEquals(entity.languageBegin, model.languageBegin)
        Assert.assertEquals(entity.languageEnd, model.languageEnd)
        Assert.assertEquals(entity.isQuechua, model.isQuechua)
        Assert.assertEquals(entity.totalEntries, model.totalEntries)
    }

    @Test
    fun testDefinitionEntityToModel() {
        val entity = DefinitionEntity(
            id = 1,
            word = "Word",
            meaning = "Meaning",
            dictionaryId = 1,
            dictionaryName = "Dictionary"
        )

        val model = entity.toDefinitionModel()

        equalsDefinitionEntityAndModel(entity, model)
    }

    @Test
    fun testDefinitionModelToEntity() {
        val model = DefinitionModel(
            id = 1,
            word = "Word",
            meaning = "Meaning",
            dictionaryId = 1,
            dictionaryName = "Dictionary"
        )
        val entity = DefinitionEntity.fromDefinitionModel(model)

        equalsDefinitionEntityAndModel(entity, model)
    }

    private fun equalsDefinitionEntityAndModel(entity: DefinitionEntity, model: DefinitionModel) {
        Assert.assertEquals(entity.id, model.id)
        Assert.assertEquals(entity.word, model.word)
        Assert.assertEquals(entity.meaning, model.meaning)
        Assert.assertEquals(entity.dictionaryId, model.dictionaryId)
        Assert.assertEquals(entity.dictionaryName, model.dictionaryName)
    }

    @Test
    fun testFavoriteEntityToDefinitionModel() {
        val entity = FavoriteEntity(
            id = 1,
            word = "Word",
            meaning = "Meaning",
            dictionaryId = 1,
            dictionaryName = "Dictionary"
        )

        val model = entity.toDefinitionModel()

        equalsFavoriteEntityAndModel(entity, model)
    }

    @Test
    fun testDefinitionModelToFavoriteEntity() {
        val model = DefinitionModel(
            id = 1,
            word = "Word",
            meaning = "Meaning",
            dictionaryId = 1,
            dictionaryName = "Dictionary"
        )
        val entity = FavoriteEntity.fromDefinitionModel(model)

        equalsFavoriteEntityAndModel(entity, model)
    }

    private fun equalsFavoriteEntityAndModel(entity: FavoriteEntity, model: DefinitionModel) {
        Assert.assertEquals(entity.id, model.id)
        Assert.assertEquals(entity.word, model.word)
        Assert.assertEquals(entity.meaning, model.meaning)
        Assert.assertEquals(entity.dictionaryId, model.dictionaryId)
        Assert.assertEquals(entity.dictionaryName, model.dictionaryName)
    }

    @Test
    fun testSearchResultEntityToModel() {
        val entity = SearchResultEntity(
            dictionaryId = 1,
            dictionaryName = "Dictionary",
            total = 81,
            definitions = mutableListOf(
                DefinitionEntity(
                    id = 1,
                    word = "Word",
                    meaning = "Meaning",
                    dictionaryId = 1,
                    dictionaryName = "Dictionary"
                ),
                DefinitionEntity(
                    id = 100,
                    word = "Word 2",
                    meaning = "Meaning 2",
                    dictionaryId = 1,
                    dictionaryName = "Dictionary"
                )
            )
        )

        val model = entity.toSearchResultModel()

        Assert.assertEquals(entity.dictionaryId, model.dictionaryId)
        Assert.assertEquals(entity.dictionaryName, model.dictionaryName)
        Assert.assertEquals(entity.total, model.total)

        entity.definitions.forEachIndexed { index, definition ->
            equalsDefinitionEntityAndModel(definition, model.definitions[index])
        }
    }
}