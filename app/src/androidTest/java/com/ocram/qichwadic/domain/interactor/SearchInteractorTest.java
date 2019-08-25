package com.ocram.qichwadic.domain.interactor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ocram.qichwadic.data.datasource.DictionaryCloudDataStore;
import com.ocram.qichwadic.data.datasource.DictionaryLocalDataStore;
import com.ocram.qichwadic.data.repository.DictionaryRepositoryImpl;
import com.ocram.qichwadic.data.repository.SearchDataRepository;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.domain.repository.DictionaryRepository;
import com.ocram.qichwadic.domain.repository.SearchRepository;
import com.ocram.qichwadic.framework.dao.AppDatabase;
import com.ocram.qichwadic.framework.net.client.RetrofitClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchInteractorTest {

    private AppDatabase appDatabase;
    private SearchInteractor searchInteractor;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb(){
        appDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        DictionaryRepository dictionaryRepository = new DictionaryRepositoryImpl(
                new DictionaryLocalDataStore(appDatabase.getDictionaryDao()),
                new DictionaryCloudDataStore(new RetrofitClient(null))
        );
        SearchRepository searchRepository = new SearchDataRepository(appDatabase.getDefinitionDao());
        searchInteractor = new SearchInteractorImpl(searchRepository, dictionaryRepository);
    }

    @Test
    public void testSearchStartsWith() {
        final String wordToSearch = "wasi";

        Dictionary dictionary = new Dictionary();
        dictionary.setId(1);
        dictionary.setQuechua(true);
        dictionary.setLanguageBegin("qu");
        dictionary.setLanguageEnd("es");
        dictionary.setTotalEntries(2);

        Definition definition = new Definition();
        definition.setId(1);
        definition.setWord("wasi");
        definition.setMeaning("casa");
        definition.setDictionaryId(dictionary.getId());

        Definition definition2 = new Definition();
        definition2.setId(2);
        definition2.setWord("qullqi");
        definition2.setMeaning("plata");
        definition2.setDictionaryId(dictionary.getId());

        Definition definition3 = new Definition();
        definition3.setId(3);
        definition3.setWord("maki");
        definition3.setMeaning("mano");
        definition3.setDictionaryId(dictionary.getId());

        appDatabase.getDictionaryDao().insertDictionaryAndDefinitions(dictionary, Arrays.asList(definition, definition2, definition3));

        List<SearchResult> searchResults = searchInteractor.queryWord(1, "es", SearchType.STARTS_WITH.getType(), wordToSearch).blockingFirst();
        Assert.assertFalse(searchResults.isEmpty());
        SearchResult searchResult = searchResults.get(0);
        Assert.assertEquals(searchResult.getDictionaryId(), dictionary.getId());
    }

    @Test
    public void testSearchStartsWithAFromQuechua() {

        Dictionary dictionary = new Dictionary();
        dictionary.setId(1);
        dictionary.setQuechua(true);
        dictionary.setLanguageBegin("qu");
        dictionary.setLanguageEnd("es");
        dictionary.setTotalEntries(2);

        List<Definition> fakeDefs = new ArrayList<>();
        for(int i = 0; i< 81; i++) {
            Definition definition = new Definition();
            definition.setId(i+1);
            definition.setWord("a" + i);
            definition.setMeaning("a" + i + "es");
            definition.setDictionaryId(dictionary.getId());
            fakeDefs.add(definition);
        }
        appDatabase.getDictionaryDao().insertDictionaryAndDefinitions(dictionary, fakeDefs);

        List<SearchResult> searchResults = searchInteractor.queryWord(1, "es", SearchType.STARTS_WITH.getType(), "a").blockingFirst();
        Assert.assertFalse(searchResults.isEmpty());

        SearchResult searchResult = searchResults.get(0);
        Assert.assertEquals(20, searchResult.getDefinitions().size());
    }

    @Test
    public void testFetchMoreResults() {

        Dictionary dictionary = new Dictionary();
        dictionary.setId(1);
        dictionary.setQuechua(true);
        dictionary.setLanguageBegin("qu");
        dictionary.setLanguageEnd("es");
        dictionary.setTotalEntries(2);

        List<Definition> fakeDefs = new ArrayList<>();
        for(int i = 0; i< 81; i++) {
            Definition definition = new Definition();
            definition.setId(i+1);
            definition.setWord("a" + (i+1));
            definition.setMeaning("a" + (i+1) + "es");
            definition.setDictionaryId(dictionary.getId());
            fakeDefs.add(definition);
        }
        appDatabase.getDictionaryDao().insertDictionaryAndDefinitions(dictionary, fakeDefs);

        List<Definition> searchResults = searchInteractor.fetchMoreResults(dictionary.getId(), SearchType.STARTS_WITH.getType(), "a", 2).blockingFirst();
        Assert.assertFalse(searchResults.isEmpty());

        Assert.assertEquals(20, searchResults.size());
        for(int i = 1; i<= searchResults.size(); i++) {
            Definition definition = searchResults.get(i - 1);
            Assert.assertEquals(20 + i, definition.getId());
            Assert.assertEquals("a" + (20 + i) , definition.getWord());
        }
    }
}
