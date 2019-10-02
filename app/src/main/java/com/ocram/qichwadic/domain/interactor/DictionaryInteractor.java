package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface DictionaryInteractor {

    Flowable<List<Dictionary>> getSavedDictionaries();

    Flowable<List<Definition>> getAllDefinitionsByDictionary(int dictionaryId);

    Flowable<Boolean> hasSavedDictionaries();

    Observable<Boolean> saveDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions);

    Observable<Boolean> removeDictionary(int id);

    Flowable<Map<String, List<Dictionary>>> getDictionariesFromCloudAndLocal();
}
