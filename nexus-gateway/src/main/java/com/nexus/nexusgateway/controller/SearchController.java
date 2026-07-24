package com.nexus.nexusgateway.controller;

import com.nexus.nexuscommons.dto.request.SearchRequest;
import com.nexus.nexuscommons.dto.response.SearchResponse;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexussearchengine.model.SearchDocument;
import com.nexus.nexussearchengine.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final AuthorizationPort authorization;

    @Autowired
    public SearchController(SearchService searchService, AuthorizationPort authorization) {
        this.searchService = searchService;
        this.authorization = authorization;
    }

    @PostMapping("/query")
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        // Global full-text search spans organisations, so it is restricted to
        // platform admins. Tenant-scoped search requires an organisationId on
        // SearchRequest and a filtered index query — tracked as a follow-up.
        authorization.requirePlatformAdmin();
        return ResponseEntity.ok(searchService.search(request));
    }

    @PostMapping("/index")
    public ResponseEntity<Void> indexDocument(@RequestBody SearchDocument document) {
        searchService.index(document);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        searchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
