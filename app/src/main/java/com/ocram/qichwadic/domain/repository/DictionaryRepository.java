package com.ocram.qichwadic.domain.repository;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;

import io.reactivex.Flowable;

public interface DictionaryRepository {

    Flowable<List<Dictionary>> getDictionaries();

    Flowable<List<Dictionary>> checkSavedDictionaries();

    Flowable<Integer> getSavedDictionariesTotal();

    Flowable<List<Definition>> getDefinitionsByDictionary(int dictionaryId);

    void saveDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions);

    void removeDictionary(int id);
}
