package com.nexus.nexussearchengine.service;

import com.nexus.nexuscommons.dto.request.SearchRequest;
import com.nexus.nexuscommons.dto.response.SearchResponse;
import com.nexus.nexussearchengine.model.SearchDocument;
import com.nexus.nexussearchengine.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    @Transactional
    public void index(SearchDocument document) {
        Instant now = Instant.now();
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(now);
        }
        document.setUpdatedAt(now);
        searchRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest request) {
        var pageable = PageRequest.of(request.page(), request.size());
        List<SearchDocument> documents = searchRepository.fullTextSearch(
                request.query(), request.indexName(), pageable);

        List<Map<String, Object>> docs = documents.stream()
                .map(doc -> Map.<String, Object>of(
                        "id", doc.getId().toString(),
                        "title", doc.getTitle(),
                        "content", doc.getContent(),
                        "indexName", doc.getIndexName()
                ))
                .toList();

        return new SearchResponse(docs, docs.size(), request.page(), request.size());
    }

    @Override
    @Transactional
    public void delete(String id) {
        searchRepository.deleteById(UUID.fromString(id));
    }
}
