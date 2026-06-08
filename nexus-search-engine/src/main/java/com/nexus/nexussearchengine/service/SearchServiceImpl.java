package com.nexus.nexussearchengine.service;

import com.nexus.nexuscommons.dto.request.SearchRequest;
import com.nexus.nexuscommons.dto.response.SearchResponse;
import com.nexus.nexussearchengine.model.SearchDocument;
import com.nexus.nexussearchengine.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;

    @Autowired
    public SearchServiceImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public void index(SearchDocument document) {
        searchRepository.save(document);
    }

    @Override
    public SearchResponse search(SearchRequest request) {
        List<SearchDocument> documents = searchRepository.findByIndexName(request.indexName());
        List<Map<String,Object>> docs = documents.stream()
                .map(doc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", doc.getId());
                    map.put("title", doc.getTitle());
                    map.put("content", doc.getContent());
                    return map;
                }).toList();

        return new SearchResponse(
                docs,
                docs.size(),
                request.page(),
                request.size()
        );
    }

    @Override
    public void delete(String id) {
        searchRepository.deleteById(id);
    }
}
