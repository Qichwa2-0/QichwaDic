package com.ocram.qichwadic.framework.net.client;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;

import io.reactivex.Flowable;

public interface Service {

    Flowable<List<Dictionary>> getDictionaries();

    Flowable<List<Definition>> getAllDefinitionsByDictionary(String entriesUrl);
}
