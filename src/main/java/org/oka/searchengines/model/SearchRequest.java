package org.oka.searchengines.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SearchRequest {
    String field;
    String value;
    String facetField;
    boolean fullText = false;
    String q;

    public SearchRequest() {
    }
}