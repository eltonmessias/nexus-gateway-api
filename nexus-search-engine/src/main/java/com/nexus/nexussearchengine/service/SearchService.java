package com.nexus.nexussearchengine.service;

import com.nexus.nexuscommons.dto.request.SearchRequest;
import com.nexus.nexuscommons.dto.response.SearchResponse;
import com.nexus.nexussearchengine.model.SearchDocument;

public interface SearchService {
    void index(SearchDocument document);
    SearchResponse search(SearchRequest request);
    void delete(String id);
}
