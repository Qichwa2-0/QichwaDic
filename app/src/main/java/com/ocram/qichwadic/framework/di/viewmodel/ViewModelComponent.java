package com.ocram.qichwadic.framework.di.viewmodel;

import com.ocram.qichwadic.framework.di.app.AppComponent;
import com.ocram.qichwadic.presentation.viewmodel.DictionaryViewModel;
import com.ocram.qichwadic.presentation.viewmodel.FavoriteViewModel;
import com.ocram.qichwadic.presentation.viewmodel.SearchViewModel;

import dagger.Component;

@ViewModelScope
@Component(modules = { SearchModule.class }, dependencies = AppComponent.class)
public interface ViewModelComponent {

    void inject(SearchViewModel searchViewModel);
    void inject(DictionaryViewModel dictionaryViewModel);
    void inject(FavoriteViewModel favoriteViewModel);
}
