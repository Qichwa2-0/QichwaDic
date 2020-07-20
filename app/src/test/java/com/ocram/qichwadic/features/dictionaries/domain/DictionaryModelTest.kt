package com.ocram.qichwadic.features.dictionaries.domain

import com.ocram.qichwadic.core.domain.model.DictionaryModel
import org.junit.Assert
import org.junit.Test

class DictionaryModelTest {

    @Test
    fun testSortDictionaries() {

        val totalDictionaries = 5
        var count = 0
        // dictionaries in odd position will be in local
        val dictionaries  = generateSequence { (++count).takeIf { it <= totalDictionaries } }.map { current ->
            DictionaryModel(
                    id = current,
                    name = "Dictionary QU $current",
                    isQuechua = true
            )
        }.toMutableList()

        val targetedDicId = dictionaries[2].id
        dictionaries[2].existsInLocal = true

        dictionaries.sort()

        Assert.assertEquals(
                "Third dictionary should now be first as it's the only local one",
                targetedDicId,
                dictionaries[0].id
        )

        dictionaries[0].existsInLocal = false

        dictionaries.sort()

        Assert.assertEquals(
                "Third dictionary should be at its place now as is no longer local",
                targetedDicId,
                dictionaries[2].id
        )


    }
}