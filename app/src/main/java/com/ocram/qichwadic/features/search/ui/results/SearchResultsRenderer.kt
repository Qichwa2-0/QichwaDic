package com.ocram.qichwadic.features.search.ui.results

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.features.search.ui.SearchViewState

interface SearchResultsRenderer {

    fun onResultsChanged(searchResults: List<SearchResultModel>?)
    fun onSearchViewStateChanged(searchViewState: SearchViewState)
    fun onFetchExtraDefinitions(definitions: List<DefinitionModel>?)
}