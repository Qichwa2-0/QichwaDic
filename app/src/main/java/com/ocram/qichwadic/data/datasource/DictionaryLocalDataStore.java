package com.ocram.qichwadic.data.datasource;

import com.ocram.qichwadic.data.repository.DictionaryRepositoryImpl;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.framework.dao.DictionaryDao;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class DictionaryLocalDataStore implements DictionaryRepositoryImpl.LocalDataStore {

    private final DictionaryDao dictionaryDao;

    @Inject
    public DictionaryLocalDataStore(DictionaryDao dictionaryDao) {
        this.dictionaryDao = dictionaryDao;
    }

    @Override
    public Flowable<List<Dictionary>> getDictionaries() {
        return dictionaryDao.getDictionaries();
    }

    @Override
    public Flowable<Integer> getSavedDictionariesTotal() {
        return dictionaryDao.getSavedDictionariesTotal();
    }

    @Override
    public void saveDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions) {
        dictionaryDao.insertDictionaryAndDefinitions(dictionary, definitions);
    }

    @Override
    public void removeDictionary(int id) {
        dictionaryDao.removeOne(id);
    }


}
