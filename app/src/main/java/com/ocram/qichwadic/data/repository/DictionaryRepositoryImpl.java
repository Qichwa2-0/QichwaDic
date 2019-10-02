package com.ocram.qichwadic.data.repository;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.repository.DictionaryRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class DictionaryRepositoryImpl implements DictionaryRepository {

    private final LocalDataStore localDataStore;

    private final CloudDataStore cloudDataStore;

    @Inject
    public DictionaryRepositoryImpl(LocalDataStore localDataStore, CloudDataStore cloudDataStore) {
        this.localDataStore = localDataStore;
        this.cloudDataStore = cloudDataStore;
    }

    public Flowable<List<Dictionary>> getDictionaries() {
        return cloudDataStore.getDictionaries();
    }

    @Override
    public Flowable<List<Dictionary>> checkSavedDictionaries() {
        return localDataStore.getDictionaries();
    }

    @Override
    public Flowable<Integer> getSavedDictionariesTotal() {
        return localDataStore.getSavedDictionariesTotal();
    }

    @Override
    public Flowable<List<Definition>> getDefinitionsByDictionary(int dictionaryId) {
        return cloudDataStore.getDefinitionsByDictionary(dictionaryId);
    }

    public void saveDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions) {
        localDataStore.saveDictionaryAndDefinitions(dictionary, definitions);
    }

    public void removeDictionary(int id) {
        localDataStore.removeDictionary(id);
    }

    public interface LocalDataStore {

        Flowable<List<Dictionary>> getDictionaries();

        Flowable<Integer> getSavedDictionariesTotal();

        void saveDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions);

        void removeDictionary(int id);
    }

    public interface CloudDataStore {

        Flowable<List<Dictionary>> getDictionaries();

        Flowable<List<Definition>> getDefinitionsByDictionary(int dictionaryId);
    }
}


