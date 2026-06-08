package com.nexus.nexussearchengine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Map;

@Document(indexName = "documents")
public class SearchDocument {
    @Id
    private String id;
    private String title;
    private String content;
    private String indexName;
    private Map<String, Object> metadata;

    public SearchDocument() {
    }

    public SearchDocument(String id, String title, String content, String indexName, Map<String, Object> metadata) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.indexName = indexName;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
