package com.ocram.qichwadic.framework.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Flowable;

@Dao
@Singleton
public interface DictionaryDao {

    @Query("SELECT COUNT(*) from dictionary")
    Flowable<Integer> getSavedDictionariesTotal();

    @Query("SELECT d.* from dictionary d order by d.is_quechua DESC, d.id")
    Flowable<List<Dictionary>> getDictionaries();

    @Query("SELECT * from dictionary where is_quechua = :fromQuechua")
    Flowable<List<Dictionary>> getDictionaries(boolean fromQuechua);

    @Query("DELETE from dictionary where id = :id")
    void removeOne(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDictionaryAndDefinitions(Dictionary dictionary, List<Definition> definitions);

}
