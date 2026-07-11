package com.nexus.nexussearchengine.repository;

import com.nexus.nexussearchengine.model.SearchDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SearchRepository extends JpaRepository<SearchDocument, UUID> {

    @Query(value = """
            SELECT *, ts_rank(search_vector, plainto_tsquery('english', :query)) AS rank
            FROM nexus_search.search_documents
            WHERE index_name = :indexName
              AND search_vector @@ plainto_tsquery('english', :query)
            ORDER BY rank DESC
            """, nativeQuery = true)
    List<SearchDocument> fullTextSearch(@Param("query") String query,
                                        @Param("indexName") String indexName,
                                        Pageable pageable);

    @Query(value = """
            SELECT *, ts_rank(search_vector, plainto_tsquery('english', :query)) AS rank
            FROM nexus_search.search_documents
            WHERE org_id = :orgId
              AND search_vector @@ plainto_tsquery('english', :query)
            ORDER BY rank DESC
            """, nativeQuery = true)
    List<SearchDocument> fullTextSearchByOrg(@Param("query") String query,
                                             @Param("orgId") UUID orgId,
                                             Pageable pageable);

    List<SearchDocument> findByIndexName(String indexName, Pageable pageable);
}
