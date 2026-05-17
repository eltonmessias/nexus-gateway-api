package com.nexus.nexuscommons.exception;

public class SearchIndexException extends NexusException{


    public SearchIndexException(String indexName, String reason) {
        super("SEARCH_INDEX_ERROR",
                String.format("Search index '%s' error: %s", indexName, reason));
    }
}
