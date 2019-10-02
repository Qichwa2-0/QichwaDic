package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.repository.DictionaryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DictionaryInteractorImpl implements DictionaryInteractor {

    private final DictionaryRepository dictionaryRepository;

    @Inject
    public DictionaryInteractorImpl(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    @Override
    public Flowable<List<Definition>> getAllDefinitionsByDictionary(int dictionaryId) {
        return dictionaryRepository
                .getDefinitionsByDictionary(dictionaryId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<Boolean> hasSavedDictionaries() {
        return dictionaryRepository.getSavedDictionariesTotal()
                .map(dictionariesTotal -> dictionariesTotal > 0)
                .onErrorReturnItem(false)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> saveDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions) {
        return Observable.<Boolean>create(e -> {
            for(Definition definition : definitions) {
                definition.setDictionaryId(dictionary.getId());
                definition.setDictionaryName(dictionary.getName());
            }
            dictionaryRepository.saveDictionaryAndDefinitions(dictionary, definitions);
            e.onNext(true);
        }).onErrorReturnItem(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeDictionary(int id) {
        return Observable.<Boolean>create(e -> {
            dictionaryRepository.removeDictionary(id);
            e.onNext(true);
        }).onErrorReturnItem(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<Map<String, List<Dictionary>>> getDictionariesFromCloudAndLocal() {
        return this.getSavedDictionaries().flatMap((List<Dictionary> dicsInLocal) -> {
            for(Dictionary dictionary : dicsInLocal){
                dictionary.setExistsInLocal(true);
            }
            return combineDictionariesFromCloudAndLocal(dicsInLocal);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Dictionary>> getSavedDictionaries(){
        return dictionaryRepository.checkSavedDictionaries();
    }

    private Flowable<Map<String, List<Dictionary>>> combineDictionariesFromCloudAndLocal(List<Dictionary> dicsInLocal){
        return dictionaryRepository.getDictionaries()
                .onErrorReturnItem(Collections.emptyList())
                .map(dicsFromCloud -> {
                    Map<String, List<Dictionary>> dictLangMap = new HashMap<>();
                    List<Dictionary> finalResult = new ArrayList<>();
                    for(Dictionary dic : dicsFromCloud){
                        if(!dicsInLocal.contains(dic)){
                            finalResult.add(dic);
                        }
                    }
                    finalResult.addAll(0, dicsInLocal);
                    for(Dictionary dictionary : finalResult) {
                        List<Dictionary> dictionaries = dictLangMap.get(dictionary.getLanguageBegin());
                        if(dictionaries == null) {
                            dictionaries = new ArrayList<>();
                        }
                        dictionaries.add(dictionary);
                        dictLangMap.put(dictionary.getLanguageBegin(), dictionaries);
                    }
                    return dictLangMap;
                });
    }
}
