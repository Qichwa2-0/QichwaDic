package com.ocram.qichwadic.presentation.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import com.ocram.qichwadic.QichwaDicApplication;
import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.interactor.DictionaryInteractor;
import com.ocram.qichwadic.framework.di.viewmodel.DaggerViewModelComponent;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DictionaryViewModel extends AndroidViewModel {

    @Inject
    public DictionaryInteractor interactor;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LiveData<Map<String, List<Dictionary>>> dictionariesByLang;
    private MutableLiveData<Boolean> localLoading = new MutableLiveData<>();
    private MutableLiveData<String> notifyMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> dicActionFailedPosition = new MutableLiveData<>();

    public DictionaryViewModel(Application application) {
        super(application);
        DaggerViewModelComponent
                .builder()
                .appComponent(((QichwaDicApplication)application).getAppComponent())
                .build()
                .inject(this);
        localLoading.setValue(true);
        loadDictionaries();
    }

    private void loadDictionaries() {
        dictionariesByLang = LiveDataReactiveStreams.fromPublisher(
                interactor.getDictionariesFromCloudAndLocal()
                        .doOnError(Throwable::printStackTrace)
                        .doOnNext((Map<String, List<Dictionary>> dictionaries) -> localLoading.setValue(false))
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public LiveData<Map<String, List<Dictionary>>> getDictionariesByLang() {
        return dictionariesByLang;
    }

    public MutableLiveData<String> getNotifyMessage() {
        return notifyMessage;
    }

    public MutableLiveData<Boolean> getLocalLoading() {
        return localLoading;
    }

    public MutableLiveData<Integer> getDicActionFailedPosition() {
        return dicActionFailedPosition;
    }

    public void downloadDictionary(int pos, final Dictionary dictionary){
        Disposable disposable =
                interactor
                        .getAllDefinitionsByDictionary(dictionary.getEntriesFile())
                        .doOnError(throwable -> onActionError(throwable, pos, dictionary, R.string.dictionary_save_error))
                        .subscribe(definitions -> saveDefinitions(pos, dictionary, definitions));
        compositeDisposable.add(disposable);
    }

    private void saveDefinitions(int pos, final Dictionary dictionary, final List<Definition> definitions){
        compositeDisposable.add(
                interactor
                        .saveDictionaryAndDefinitions(dictionary, definitions)
                        .doOnError(throwable -> onActionError(throwable, pos, dictionary, R.string.dictionary_save_error))
                        .subscribe(result -> onActionFinished(dictionary, R.string.dictionary_save_success))
        );
    }

    public void removeDictionary(int pos, Dictionary dictionary){
        compositeDisposable.add(
          interactor
                  .removeDictionary(dictionary.getId())
                  .doOnError(throwable -> onActionError(throwable, pos, dictionary, R.string.dictionary_delete_error))
                  .subscribe(result -> onActionFinished(dictionary, R.string.dictionary_delete_success))
        );
    }

    private void onActionError(Throwable throwable, int pos, Dictionary dictionary, int resId) {
        throwable.printStackTrace();
        notifyMessage.postValue(getApplication().getString(resId, dictionary.getName()));
        dicActionFailedPosition.postValue(pos);
    }

    private void onActionFinished(Dictionary dictionary, int resId) {
        dictionary.setDownloading(false);
        notifyMessage.postValue(getApplication().getString(resId, dictionary.getName()));
    }
}
