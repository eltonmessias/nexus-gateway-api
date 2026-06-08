package com.nexus.nexussearchengine.repository;

import com.nexus.nexussearchengine.model.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchRepository extends ElasticsearchRepository<SearchDocument, String> {
    List<SearchDocument> findByIndexName(String indexName);
}
